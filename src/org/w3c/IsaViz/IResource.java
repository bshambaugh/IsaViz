/*   FILE: IResource.java
 *   DATE OF CREATION:   10/18/2001
 *   AUTHOR :            Emmanuel Pietriga (emmanuel@w3.org)
 *   MODIF:              Fri Aug 08 17:37:31 2003 by Emmanuel Pietriga (emmanuel@w3.org, emmanuel@claribole.net)
 */

/*
 *
 *  (c) COPYRIGHT World Wide Web Consortium, 1994-2001.
 *  Please first read the full copyright statement in file copyright.html
 *
 */ 



package org.w3c.IsaViz;

import java.util.Vector;
import java.util.Hashtable;
import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.xerox.VTM.glyphs.*;
import com.xerox.VTM.engine.VirtualSpaceManager;
import com.xerox.VTM.engine.VirtualSpace;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.RDFException;

/*Our internal model class for RDF Resources*/

class IResource extends INode {

    /**returns the substring of a Jena AnonID that is unique for each anonymous resource (i.e. what is after the first stroke)
     * e.g. e184cb:ea21ce7dcf:-7fda  will return 7fda because everything that else is common to all AnonIDs fro a given implementation/execution of the application
     */
    public static String getJenaAnonId(AnonId id){
	StringBuffer sb=new StringBuffer(id.toString());
	int i=0;
	while (i<sb.length()){
	    if (sb.charAt(i)=='-'){sb.delete(0,i+1);break;}
	    i++;
	}
	return sb.toString();
    }

    Vector incomingPredicates;   //list of IProperty - null if empty
    Vector outgoingPredicates;   //list of IProperty - null if empty

    private boolean anonymous=false;   //anonymous resource or not
    private String anonymousID;        //anonymous id (null if not anonymous)
//     private String namespace;          //namespace+localname = URI
//     private String localname;
    private String uri;
    private boolean fragmentID=false;

    private String label;  //set only if there is an rdfs:label property for this resource

    String mapID;

    Glyph gl1;
    VText gl2;    //if not text has been entered yet, this glyph is null (use this test to find out if there is text)

    /**
     *@param r Jena resource representing this node
     */
    public IResource(Resource r){
	fillIndex=ConfigManager.defaultRFIndex;
	strokeIndex=ConfigManager.defaultRTBIndex;
	try {
	    if (r.isAnon()){
		anonymous=true;
		anonymousID=Editor.ANON_NODE+IResource.getJenaAnonId(r.getId());
		fragmentID=false;
	    }
	    else {
		anonymous=false;
		uri=r.getURI();
		if (uri.startsWith(Editor.BASE_URI)){this.setURIFragment(true);}
	    }
	}
	catch (RDFException ex){System.err.println("Error: IResouce(Resource - Jena): "+ex);}
    }

    /**Create a new IResource from scratch (information will be added later)*/
    public IResource(){
	fillIndex=ConfigManager.defaultRFIndex;
	strokeIndex=ConfigManager.defaultRTBIndex;
    }

    //the split between namespace and localname is made automatically by IsaViz/Jena (it is just a guess)
    void setURI(String u){
	this.uri=u;
    }

    void setURIFragment(boolean b){
	fragmentID=b;
    }

    boolean isURIFragment(){
	return fragmentID;
    }

    //id MUST contain the anon prefix
    void setAnonymousID(String id){anonymousID=id;}

    boolean isAnon(){return anonymous;}

    void setAnon(boolean b){anonymous=b;}
    
    public String getIdentity(){
	if (anonymous){return anonymousID;}
	else {return uri;}
    }

    public String getGraphLabel(){
	if (anonymous){return anonymousID;}
	else {
	    try {
		String res=uri;
		if (fragmentID && res.startsWith(Editor.BASE_URI)){res=res.substring(Editor.BASE_URI.length());}
		return res;
	    }
	    catch (NullPointerException ex){return "";}
	}
    }

    //rdfs:label property (set to null if empty)
    public void setLabel(String s){label=(s.length()==0) ? null : s;}

    //rdfs:label property (can be null)
    public String getLabel(){return label;}

    public void setMapID(String s){mapID=s;}

    public String getMapID(){return mapID;}

    public void addIncomingPredicate(IProperty p){
	if (incomingPredicates==null){
	    incomingPredicates=new Vector();
	    incomingPredicates.add(p);
	}
	else {
	    if (!incomingPredicates.contains(p)){incomingPredicates.add(p);}
	}
    }

    public void removeIncomingPredicate(IProperty p){
	if (incomingPredicates!=null && incomingPredicates.contains(p)){
	    incomingPredicates.remove(p);
	    if (incomingPredicates.isEmpty()){incomingPredicates=null;}
	}
    }

    /**returns null if none*/
    public Vector getIncomingPredicates(){
	return incomingPredicates;
    }

    public void addOutgoingPredicate(IProperty p){
	if (outgoingPredicates==null){
	    outgoingPredicates=new Vector();
	    outgoingPredicates.add(p);
// 	    System.err.println(getIdent()+" -a-> "+Utils.vectorOfObjectsAsCSString(outgoingPredicates));
	}
	else {
	    if (!outgoingPredicates.contains(p)){outgoingPredicates.add(p);} //System.err.println(getIdent()+" -b-> "+Utils.vectorOfObjectsAsCSString(outgoingPredicates));}
	    //else {System.err.println(getIdent()+" -c-> "+Utils.vectorOfObjectsAsCSString(outgoingPredicates));}
	}
    }

    public void removeOutgoingPredicate(IProperty p){
	if (outgoingPredicates!=null && outgoingPredicates.contains(p)){
	    outgoingPredicates.remove(p);
	    if (outgoingPredicates.isEmpty()){outgoingPredicates=null;}
	}
    }

    /**returns null if none*/
    public Vector getOutgoingPredicates(){
	return outgoingPredicates;
    }
    
    /**selects this node (and assigns colors to glyph and text)*/
    public void setSelected(boolean b){
	super.setSelected(b);
	if (this.isVisuallyRepresented()){
	    if (selected){
		gl1.setHSVColor(ConfigManager.selFh,ConfigManager.selFs,ConfigManager.selFv);
		gl1.setHSVbColor(ConfigManager.selTh,ConfigManager.selTs,ConfigManager.selTv);
		if (gl2!=null){gl2.setHSVColor(ConfigManager.selTh,ConfigManager.selTs,ConfigManager.selTv);}
		VirtualSpace vs=Editor.vsm.getVirtualSpace(Editor.mainVirtualSpace);
		vs.onTop(gl1);vs.onTop(gl2);
	    }
	    else {
		if (commented){
		    gl1.setHSVColor(ConfigManager.comFh,ConfigManager.comFs,ConfigManager.comFv);
		    gl1.setHSVbColor(ConfigManager.comTh,ConfigManager.comTs,ConfigManager.comTv);
		    if (gl2!=null){gl2.setHSVColor(ConfigManager.comTh,ConfigManager.comTs,ConfigManager.comTv);}
		}
		else {
		    gl1.setColor(ConfigManager.colors[fillIndex]);
		    gl1.setBorderColor(ConfigManager.colors[strokeIndex]);
		    if (gl2!=null){gl2.setColor(ConfigManager.colors[strokeIndex]);}
		}
	    }
	}
    }

    public void comment(boolean b,Editor e){
	commented=b;
	if (commented){//comment
	    if (this.isVisuallyRepresented()){
		gl1.setHSVColor(ConfigManager.comFh,ConfigManager.comFs,ConfigManager.comFv);
		gl1.setHSVbColor(ConfigManager.comTh,ConfigManager.comTs,ConfigManager.comTv);
		if (gl2!=null){gl2.setHSVColor(ConfigManager.comTh,ConfigManager.comTs,ConfigManager.comTv);}
	    }
	    if (incomingPredicates!=null){
		for (int i=0;i<incomingPredicates.size();i++){
		    e.commentPredicate((IProperty)incomingPredicates.elementAt(i),true);
		}
	    }
	    if (outgoingPredicates!=null){
		for (int i=0;i<outgoingPredicates.size();i++){
		    e.commentPredicate((IProperty)outgoingPredicates.elementAt(i),true);
		}
	    }
	}
	else {//uncomment
	    if (this.isVisuallyRepresented()){
		gl1.setColor(ConfigManager.colors[fillIndex]);
		gl1.setBorderColor(ConfigManager.colors[strokeIndex]);
		if (gl2!=null){gl2.setColor(ConfigManager.colors[strokeIndex]);}
	    }
	    if (incomingPredicates!=null){
		for (int i=0;i<incomingPredicates.size();i++){
		    e.commentPredicate((IProperty)incomingPredicates.elementAt(i),false);
		}
	    }
	    if (outgoingPredicates!=null){
		for (int i=0;i<outgoingPredicates.size();i++){
		    e.commentPredicate((IProperty)outgoingPredicates.elementAt(i),false);
		}
	    }
	}
    }

    public void setVisible(boolean b){
	if (gl1!=null){gl1.setVisible(b);gl1.setSensitivity(b);}
	if (gl2!=null){gl2.setVisible(b);gl2.setSensitivity(b);}
    }

    public void setGlyph(Glyph e){
	gl1=e;
	gl1.setType(Editor.resShapeType);   //means resource glyph (glyph type is a quick way to retrieve glyphs from VTM)
	gl1.setOwner(this);
    }

    public void setGlyphText(VText t){
	if (t!=null){
	    gl2=t;
	    gl2.setType(Editor.resTextType);  //means resource text (glyph type is a quick way to retrieve glyphs from VTM)
	    gl2.setOwner(this);
	}
	else {gl2=null;}
    }

    public Glyph getGlyph(){
	return gl1;
    }

    public VText getGlyphText(){
	return gl2;
    }

    public Element toISV(Document d,ISVManager e,Hashtable bitmapImages,File prjFile,Vector fonts){
	Element res=d.createElementNS(Editor.isavizURI,"isv:iresource");
	Element identif=d.createElementNS(Editor.isavizURI,"isv:URIorID");
	if (!anonymous){
	    Element uriEL=d.createElementNS(Editor.isavizURI,"isv:uri");
	    uriEL.appendChild(d.createTextNode(uri));
	    if (fragmentID){uriEL.setAttribute("fID","true");}
	    identif.appendChild(uriEL);
	}
	else {
	    if (anonymousID!=null){
		Element anonIDEL=d.createElementNS(Editor.isavizURI,"isv:anonID");
		//do not save prefix since it is a user preference (read from the config file)
		anonIDEL.appendChild(d.createTextNode(Utils.erasePrefix(anonymousID)));
		identif.appendChild(anonIDEL);
	    }
	}
	res.appendChild(identif);
	if (this.isVisuallyRepresented()){
	    //it might actually be worth to save the geom info when visibility=hidden (since it exists)
	    //for now, we do not save anything geom info, no matter whether display=none or visibility=hidden
	    res.setAttribute("display","true");
	    if (table){res.setAttribute("table","true");}//omitted if node-edge (but parser will understand table=false)
	    if (gl2!=null){
		identif.setAttribute("x",String.valueOf(gl2.vx));
		identif.setAttribute("y",String.valueOf(gl2.vy));
	    }
	    res.setAttribute("x",String.valueOf(gl1.vx));
	    res.setAttribute("y",String.valueOf(gl1.vy));
	    res.setAttribute("fill",String.valueOf(fillIndex));
	    res.setAttribute("stroke",String.valueOf(strokeIndex));
	    if (gl1.getStroke()!=null){
		if (gl1.getStroke().getLineWidth()!=Glyph.DEFAULT_STROKE_WIDTH){
		    res.setAttribute("stroke-width",String.valueOf(gl1.getStroke().getLineWidth()));
		}
		if (gl1.getStroke().getDashArray()!=null){
		    res.setAttribute("stroke-dasharray",Utils.arrayOffloatAsCSStrings(gl1.getStroke().getDashArray()));
		}
	    }
	    if (this.getTextAlign()!=Style.TA_CENTER.intValue()){
		res.setAttribute("text-align",String.valueOf(this.getTextAlign()));
	    }
	    if (gl1 instanceof VEllipse){
		res.setAttribute("shape",Style.ELLIPSE.toString());
		res.setAttribute("w",String.valueOf(((RectangularShape)gl1).getWidth()));
		res.setAttribute("h",String.valueOf(((RectangularShape)gl1).getHeight()));
	    }
	    else if (gl1 instanceof VRectangle){
		res.setAttribute("shape",Style.RECTANGLE.toString());
		res.setAttribute("w",String.valueOf(((RectangularShape)gl1).getWidth()));
		res.setAttribute("h",String.valueOf(((RectangularShape)gl1).getHeight()));
	    }
	    else if (gl1 instanceof VRoundRect){
		res.setAttribute("shape",Style.ROUND_RECTANGLE.toString());
		res.setAttribute("w",String.valueOf(((RectangularShape)gl1).getWidth()));
		res.setAttribute("h",String.valueOf(((RectangularShape)gl1).getHeight()));
	    }
	    else if (gl1 instanceof VImage){
		//here we must save the bitmap icon using a mechanism close to what we have for SVG export in the ZVTM
		File bitmapFile=Utils.exportBitmap((VImage)gl1,prjFile,bitmapImages);
		/*relative URI as the png files are supposed
		  to be in img_subdir w.r.t the SVG file*/
		if (bitmapFile!=null){
		    res.setAttribute("shape","icon");
		    res.setAttributeNS(com.xerox.VTM.svg.SVGWriter.xlinkURI,"xlink:href",ISVManager.img_subdir.getName()+"/"+bitmapFile.getName());
		}
		else {//if the bitmap export process fails in any way, replace it by a standard ellipse
		    res.setAttribute("shape",Style.ELLIPSE.toString());
		}
		res.setAttribute("w",String.valueOf(((RectangularShape)gl1).getWidth()));
		res.setAttribute("h",String.valueOf(((RectangularShape)gl1).getHeight()));
	    }
	    else if (gl1 instanceof VPolygon){
		res.setAttribute("shape","{"+((VPolygon)gl1).getVerticesAsText()+"}");
	    }
	    else if (gl1 instanceof VShape){
		res.setAttribute("shape","["+((VShape)gl1).getVerticesAsText()+"]");
		res.setAttribute("sz",String.valueOf(gl1.getSize()));
		res.setAttribute("or",String.valueOf(gl1.getOrient()));
	    }
	    else if (gl1 instanceof VCircle){
		res.setAttribute("shape",Style.CIRCLE.toString());
		res.setAttribute("sz",String.valueOf(gl1.getSize()));
	    }
	    else if (gl1 instanceof VDiamond){
		res.setAttribute("shape",Style.DIAMOND.toString());
		res.setAttribute("sz",String.valueOf(gl1.getSize()));
	    }
	    else if (gl1 instanceof VOctagon){
		res.setAttribute("shape",Style.OCTAGON.toString());
		res.setAttribute("sz",String.valueOf(gl1.getSize()));
	    }
	    else if (gl1 instanceof VTriangle){
		if (gl1.getOrient()==(float)Math.PI){
		    res.setAttribute("shape",Style.TRIANGLES.toString());
		}
		else if (gl1.getOrient()==(float)-Math.PI/2.0f){
		    res.setAttribute("shape",Style.TRIANGLEE.toString());
		}
		else if (gl1.getOrient()==(float)Math.PI/2.0f){
		    res.setAttribute("shape",Style.TRIANGLEW.toString());
		}
		else {
		    res.setAttribute("shape",Style.TRIANGLEN.toString());
		}
		res.setAttribute("sz",String.valueOf(gl1.getSize()));
	    }
	    else {//for robustness
		res.setAttribute("shape",Style.ELLIPSE.toString());
		res.setAttribute("w",String.valueOf(((RectangularShape)gl1).getWidth()));
		res.setAttribute("h",String.valueOf(((RectangularShape)gl1).getHeight()));
	    }
	    //save font
	    if (gl2!=null){
		int index=fonts.indexOf(gl2.getFont());
		if (index==-1){
		    fonts.add(gl2.getFont());
		    index=fonts.size()-1;
		}
		//do not save font info if font is default zvtm/graph font
		if (index!=0){res.setAttribute("font",String.valueOf(index));}
	    }
	}
	else {
	    res.setAttribute("display","false");
	}
	if (anonymous){res.setAttribute("isAnon","true");}  //do not put this attr if not anon (although isAnon="false" is supported by the ISV loader)
	if (commented){res.setAttribute("commented","true");}  //do not put this attr if not commented (although commented="false" is supported by the ISV loader)
	res.setAttribute("id",e.getPrjId(this));
	return res;
    }

    public String toString(){return super.toString()+" "+getIdentity();}

    //a meaningful string representation of this IResource
    public String getText(){return (getIdentity()==null) ? "" : getIdentity();}

    public void displayOnTop(){
	Editor.vsm.getVirtualSpace(Editor.mainVirtualSpace).onTop(gl1);
	Editor.vsm.getVirtualSpace(Editor.mainVirtualSpace).onTop(gl2);
    }

    public void setFillColor(int i){//index of color in ConfigManager.colors
	fillIndex=i;
	gl1.setColor(ConfigManager.colors[fillIndex]);
    }

    public int getFillIndex(){return fillIndex;}
    
    public void setStrokeColor(int i){//index of color in ConfigManager.colors
	strokeIndex=i;
	gl1.setBorderColor(ConfigManager.colors[strokeIndex]);
	if (gl2!=null){gl2.setColor(ConfigManager.colors[strokeIndex]);}
    }

    public int getStrokeIndex(){return strokeIndex;}

    /*returns true if this resource has a property rdf:type with value type*/
    public boolean hasRDFType(String type){
	if (outgoingPredicates!=null){
	    IProperty p;
	    for (int i=0;i<outgoingPredicates.size();i++){
		p=(IProperty)outgoingPredicates.elementAt(i);
		if (p.getIdent().equals(GraphStylesheet._rdfType) && (p.getObject() instanceof IResource) && ((IResource)p.getObject()).getIdentity().equals(type)){return true;}
	    }
	}
	return false;
    }

}
