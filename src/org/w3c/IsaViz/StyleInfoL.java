/*   FILE: StyleInfoL.java
 *   DATE OF CREATION:   Tue Apr 01 14:25:37 2003
 *   AUTHOR :            Emmanuel Pietriga (emmanuel@w3.org)
 *   MODIF:              Wed Aug 06 10:30:00 2003 by Emmanuel Pietriga (emmanuel@w3.org, emmanuel@claribole.net)
 */ 

/*
 *
 *  (c) COPYRIGHT World Wide Web Consortium, 1994-2001.
 *  Please first read the full copyright statement in file copyright.html
 *
 */ 


package org.w3c.IsaViz;

import java.awt.Color;

class StyleInfoL extends StyleInfo {

    Integer shape;
    float[] vertices;
    Float orientation;

    java.net.URL icon;

    Integer text_align;

    StyleInfoL(){

    }
    
    void applyStyle(Style s){
	if (s!=null){
	    if (this.fill==null && s.getFill()!=null){this.fill=s.getFill();}
	    if (this.stroke==null && s.getStroke()!=null){this.stroke=s.getStroke();}
	    if (this.strokeWidth==null && s.getStrokeWidth()!=null){this.strokeWidth=s.getStrokeWidth();}
	    if (this.fontFamily==null && s.getFontFamily()!=null){this.fontFamily=s.getFontFamily();}
	    if (this.fontSize==null && s.getFontSize()!=null){this.fontSize=s.getFontSize();}
	    if (this.fontWeight==null && s.getFontWeight()!=null){this.fontWeight=s.getFontWeight();}
	    if (this.fontStyle==null && s.getFontStyle()!=null){this.fontStyle=s.getFontStyle();}
	    if (this.shape==null && s.getShape()!=null){
		this.shape=s.getShape();
		if (this.shape.equals(Style.CUSTOM_SHAPE)){
		    this.vertices=s.getVertexList();
		    this.orientation=s.getShapeOrient();
		}
	    }
	    if (this.icon==null && s.getIcon()!=null){this.icon=s.getIcon();}
	    if (this.text_align==null && s.getTextAlignment()!=null){this.text_align=s.getTextAlignment();}
	    if (this.strokeDashArray==null && s.getStrokeDashArray()!=null){this.strokeDashArray=s.getStrokeDashArray();}
	}
    }

    void applyLayout(Integer l){
	if (this.layout==null && l!=null && (l.equals(GraphStylesheet.TABLE_FORM) || l.equals(GraphStylesheet.NODE_EDGE))){
	    this.layout=l;//set it only if it hasn't been set yet by an higher priority rule
	}
    }

    void applyVisibility(Integer v){
	if (this.visibility==null && v!=null && (v.equals(GraphStylesheet.DISPLAY_NONE) || v.equals(GraphStylesheet.VISIBILITY_HIDDEN) || v.equals(GraphStylesheet.VISIBILITY_VISIBLE))){
	    this.visibility=v;//set it only if it hasn't been set yet by an higher priority rule
	}
    }

    boolean isFullySpecified(){
	if (fontFamily==null || fontSize==null || fontWeight==null || fontStyle==null || (shape==null && icon==null) || visibility==null || layout==null || strokeWidth==null || stroke==null || fill==null || text_align==null || strokeDashArray==null){return false;}
	else return true;
    }

    boolean isDisplayNone(){
	if (visibility!=null && visibility.equals(GraphStylesheet.DISPLAY_NONE)){return true;}
	else {return false;}
    }

    boolean isVisibilityHiddenAndShapeSpecified(){
	if (visibility!=null && visibility.equals(GraphStylesheet.VISIBILITY_HIDDEN) && (shape!=null || icon!=null)){return true;}
	else {return false;}
    }

    Color getFillColor(){
	if (fill==null){return GraphStylesheet.DEFAULT_LITERAL_FILL;}
	else {return fill;}
    }

    Color getStrokeColor(){
	if (stroke==null){return GraphStylesheet.DEFAULT_LITERAL_STROKE;}
	else {return stroke;}
    }

    Float getStrokeWidth(){
	if (strokeWidth==null){return GraphStylesheet.DEFAULT_LITERAL_STROKE_WIDTH;}
	else {return strokeWidth;}
    }

    String getFontFamily(){
	if (fontFamily==null){return GraphStylesheet.DEFAULT_LITERAL_FONT_FAMILY;}
	else {return fontFamily;}
    }

    Integer getFontSize(){
	if (fontSize==null){return GraphStylesheet.DEFAULT_LITERAL_FONT_SIZE;}
	else {return fontSize;}
    }

    Short getFontWeight(){
	if (fontWeight==null){return GraphStylesheet.DEFAULT_LITERAL_FONT_WEIGHT;}
	else {return fontWeight;}
    }

    Short getFontStyle(){
	if (fontStyle==null){return GraphStylesheet.DEFAULT_LITERAL_FONT_STYLE;}
	else {return fontStyle;}
    }

    Integer getVisibility(){
	if (visibility==null){return GraphStylesheet.DEFAULT_LITERAL_VISIBILITY;}
	else {return visibility;}
    }

    Integer getLayout(){
	if (layout==null){return GraphStylesheet.DEFAULT_LITERAL_LAYOUT;}
	else {return layout;}
    }

    Object getShape(){//result is one of Style.{ELLIPSE,RECTANGLE,CIRCLE,DIAMOND,OCTAGON,TRIANGLEN,TRIANGLES,TRIANGLEE,TRIANGLEW} or a CustomShape
	//if (shape==null){return GraphStylesheet.DEFAULT_LITERAL_SHAPE;}
	/*cannot do the above anymore, as we need to know if the shape was specified or not
	 (so that icon can be applied if specified and not conflicting with shape*/
	if (shape==null){return null;}
	else if (shape.equals(Style.CUSTOM_SHAPE)){
	    return new CustomShape(vertices,orientation);
	}
	else if (shape.equals(Style.CUSTOM_POLYGON)){
	    return new CustomPolygon(vertices);
	}
	else {
	    return shape;
	}
    }

    /*can return null if not specified*/
    java.net.URL getIcon(){
	return icon;
    }

    Integer getTextAlignment(){
	if (text_align==null){return GraphStylesheet.DEFAULT_LITERAL_TEXT_ALIGN;}
	else {return text_align;}
    }
    
}
