/*   FILE: IsvAppletEvtHdlr.java
 *   DATE OF CREATION:   Wed Apr 23 09:31:11 2003
 *   AUTHOR :            Emmanuel Pietriga (emmanuel@w3.org)
 *   MODIF:              Fri Apr 25 16:23:18 2003 by Emmanuel Pietriga (emmanuel@w3.org, emmanuel@claribole.net)
 */

/*
 *
 *  (c) COPYRIGHT World Wide Web Consortium, 1994-2001.
 *  Please first read the full copyright statement in file copyright.html
 *
 */


package org.w3c.IsaViz.applet;

import java.util.Vector;
import java.awt.Point;
import com.xerox.VTM.engine.*;
import com.xerox.VTM.glyphs.*;

public class IsvAppletEvtHdlr extends AppletEventHandler {

    IsvBrowser application;

    long lastX,lastY,lastJPX,lastJPY;    //remember last mouse coords to compute translation  (dragging)
    float tfactor;
    float cfactor=50.0f;
    long x1,y1,x2,y2;                     //remember last mouse coords to display selection rectangle (dragging)

    VSegment navSeg;

    Camera activeCam;

    boolean zoomingInRegion=false;
    boolean manualLeftButtonMove=false;
    boolean manualRightButtonMove=false;

    IsvAppletEvtHdlr(IsvBrowser app){
	this.application=app;
    }

    public void press1(ViewPanel v,int mod,int jpx,int jpy){
	application.rememberLocation(v.cams[0].getLocation());
	if (mod==NO_MODIFIER || mod==SHIFT_MOD){
	    manualLeftButtonMove=true;
	    lastJPX=jpx;
	    lastJPY=jpy;
	    v.setDrawDrag(true);
	    IsvBrowser.vsm.activeView.mouse.setSensitivity(false);  //because we would not be consistent  (when dragging the mouse, we computeMouseOverList, but if there is an anim triggered by {X,Y,A}speed, and if the mouse is not moving, this list is not computed - so here we choose to disable this computation when dragging the mouse with button 3 pressed)
	    activeCam=application.vsm.getActiveCamera();
	}
	else if (mod==CTRL_MOD){
	    zoomingInRegion=true;
	    x1=v.getMouse().vx;
	    y1=v.getMouse().vy;
	    v.setDrawRect(true);
	}
    }

    public void release1(ViewPanel v,int mod,int jpx,int jpy){
	if (zoomingInRegion){
	    v.setDrawRect(false);
	    x2=v.getMouse().vx;
	    y2=v.getMouse().vy;
	    if ((Math.abs(x2-x1)>=4) && (Math.abs(y2-y1)>=4)){
		IsvBrowser.vsm.centerOnRegion(IsvBrowser.vsm.getActiveCamera(),IsvBrowser.ANIM_MOVE_LENGTH,x1,y1,x2,y2);
	    }
	    zoomingInRegion=false;
	}
	else if (manualLeftButtonMove){
	    IsvBrowser.vsm.animator.Xspeed=0;
	    IsvBrowser.vsm.animator.Yspeed=0;
	    IsvBrowser.vsm.animator.Aspeed=0;
	    v.setDrawDrag(false);
	    IsvBrowser.vsm.activeView.mouse.setSensitivity(true);
	    manualLeftButtonMove=false;
	}
    }

    public void click1(ViewPanel v,int mod,int jpx,int jpy,int clickNumber){
	Glyph g=v.lastGlyphEntered();
	if (g!=null){
	    IsvBrowser.vsm.centerOnGlyph(g,v.cams[0],IsvBrowser.ANIM_MOVE_LENGTH);
	}
    }

    public void press2(ViewPanel v,int mod,int jpx,int jpy){application.loadSVG();}
    public void release2(ViewPanel v,int mod,int jpx,int jpy){}
    public void click2(ViewPanel v,int mod,int jpx,int jpy,int clickNumber){}

    public void press3(ViewPanel v,int mod,int jpx,int jpy){
	application.rememberLocation(v.cams[0].getLocation());
	lastJPX=jpx;
	lastJPY=jpy;
	//ZGRViewer.vsm.setActiveCamera(v.cams[0]);
	v.setDrawDrag(true);
	IsvBrowser.vsm.activeView.mouse.setSensitivity(false);  //because we would not be consistent  (when dragging the mouse, we computeMouseOverList, but if there is an anim triggered by {X,Y,A}speed, and if the mouse is not moving, this list is not computed - so here we choose to disable this computation when dragging the mouse with button 3 pressed)
	activeCam=application.vsm.getActiveCamera();
    }

    public void release3(ViewPanel v,int mod,int jpx,int jpy){
	IsvBrowser.vsm.animator.Xspeed=0;
	IsvBrowser.vsm.animator.Yspeed=0;
	IsvBrowser.vsm.animator.Aspeed=0;
	v.setDrawDrag(false);
	IsvBrowser.vsm.activeView.mouse.setSensitivity(true);
    }

    public void click3(ViewPanel v,int mod,int jpx,int jpy,int clickNumber){
	Glyph g=v.lastGlyphEntered();
	if (g!=null){
	    IsvBrowser.vsm.centerOnGlyph(g,v.cams[0],IsvBrowser.ANIM_MOVE_LENGTH);
	}
    }

    public void mouseMoved(ViewPanel v,int jpx,int jpy){}

    public void mouseDragged(ViewPanel v,int mod,int buttonNumber,int jpx,int jpy){
	if (buttonNumber==3 || (buttonNumber==1 && (mod==NO_MODIFIER || mod==SHIFT_MOD))){
	    tfactor=(activeCam.focal+Math.abs(activeCam.altitude))/activeCam.focal;
	    if (mod==SHIFT_MOD) {
		application.vsm.animator.Xspeed=0;
		application.vsm.animator.Yspeed=0;
 		application.vsm.animator.Aspeed=(activeCam.altitude>0) ? (long)((lastJPY-jpy)*(tfactor/cfactor)) : (long)((lastJPY-jpy)/(tfactor*cfactor));  //50 is just a speed factor (too fast otherwise)
	    }
	    else {
		application.vsm.animator.Xspeed=(activeCam.altitude>0) ? (long)((jpx-lastJPX)*(tfactor/cfactor)) : (long)((jpx-lastJPX)/(tfactor*cfactor));
		application.vsm.animator.Yspeed=(activeCam.altitude>0) ? (long)((lastJPY-jpy)*(tfactor/cfactor)) : (long)((lastJPY-jpy)/(tfactor*cfactor));
		application.vsm.animator.Aspeed=0;
	    }
	}
    }

    public void enterGlyph(Glyph g){
	super.enterGlyph(g);
    }

    public void exitGlyph(Glyph g){
	super.exitGlyph(g);
    }

}
