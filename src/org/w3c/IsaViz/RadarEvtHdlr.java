/*   FILE: RadarEvtHdlr.java
 *   DATE OF CREATION:   11/05/2002
 *   AUTHOR :            Emmanuel Pietriga (emmanuel@w3.org)
 *   MODIF:              Wed May 14 15:14:01 2003 by Emmanuel Pietriga (emmanuel@w3.org, emmanuel@claribole.net)
 */

/*
 *
 *  (c) COPYRIGHT World Wide Web Consortium, 1994-2001.
 *  Please first read the full copyright statement in file copyright.html
 *
 */ 



package org.w3c.IsaViz;

// import java.util.Vector;
import java.awt.event.KeyEvent;
import com.xerox.VTM.engine.*;
import com.xerox.VTM.glyphs.*;

public class RadarEvtHdlr extends AppEventHandler {

    Editor application;

    private boolean draggingRegionRect=false;

    RadarEvtHdlr(Editor app){
	this.application=app;
    }

    public void press1(ViewPanel v,int mod,int jpx,int jpy){
// 	if (v.lastGlyphEntered()!=null){
	    Editor.vsm.stickToMouse(application.observedRegion);  //necessarily observedRegion glyph (there is no other glyph)
	    Editor.vsm.activeView.mouse.setSensitivity(false);
	    draggingRegionRect=true;
// 	}
    }

    public void release1(ViewPanel v,int mod,int jpx,int jpy){
	if (draggingRegionRect){
	    Editor.vsm.activeView.mouse.setSensitivity(true);
	    Editor.vsm.unstickFromMouse();
	    draggingRegionRect=false;
	}
    }

    public void click1(ViewPanel v,int mod,int jpx,int jpy,int clickNumber){}

    public void press2(ViewPanel v,int mod,int jpx,int jpy){
	Editor.vsm.getGlobalView(Editor.vsm.getVirtualSpace(Editor.mainVirtualSpace).getCamera(1),500);
	application.cameraMoved();
    }
    public void release2(ViewPanel v,int mod,int jpx,int jpy){}
    public void click2(ViewPanel v,int mod,int jpx,int jpy,int clickNumber){}

    public void press3(ViewPanel v,int mod,int jpx,int jpy){
	Editor.vsm.stickToMouse(application.observedRegion);  //necessarily observedRegion glyph (there is no other glyph)
	Editor.vsm.activeView.mouse.setSensitivity(false);
	draggingRegionRect=true;
    }

    public void release3(ViewPanel v,int mod,int jpx,int jpy){
	if (draggingRegionRect){
	    Editor.vsm.activeView.mouse.setSensitivity(true);
	    Editor.vsm.unstickFromMouse();
	    draggingRegionRect=false;
	}
    }

    public void click3(ViewPanel v,int mod,int jpx,int jpy,int clickNumber){}

    public void mouseMoved(ViewPanel v,int jpx,int jpy){}

    public void mouseDragged(ViewPanel v,int mod,int buttonNumber,int jpx,int jpy){
	if (draggingRegionRect){
	    application.updateMainViewFromRadar();
	}
    }

    public void enterGlyph(Glyph g){
	//super.enterGlyph(g);
    }

    public void exitGlyph(Glyph g){
	//super.exitGlyph(g);
    }

    public void Ktype(ViewPanel v,char c,int code,int mod){}

    public void Kpress(ViewPanel v,char c,int code,int mod){
	if (mod==0){//pressing no modifier
	    if (code==KeyEvent.VK_PAGE_UP){application.getHigherView();}
	    else if (code==KeyEvent.VK_PAGE_DOWN){application.getLowerView();}
	    else if (code==KeyEvent.VK_HOME){application.getGlobalView();}
	    else if (code==KeyEvent.VK_SPACE){application.centerRadarView();}
	    else if (code==KeyEvent.VK_UP){application.translateView(Editor.MOVE_UP);}
	    else if (code==KeyEvent.VK_DOWN){application.translateView(Editor.MOVE_DOWN);}
	    else if (code==KeyEvent.VK_LEFT){application.translateView(Editor.MOVE_LEFT);}
	    else if (code==KeyEvent.VK_RIGHT){application.translateView(Editor.MOVE_RIGHT);}
	}
	else if (mod==2){
	    if (code==KeyEvent.VK_Z){application.undo();}
	    else if (code==KeyEvent.VK_X){application.cutSelection();}
	    else if (code==KeyEvent.VK_C){application.copySelection();}
	    else if (code==KeyEvent.VK_V){application.pasteSelection(v.getMouse().vx,v.getMouse().vy);}
	    else if (code==KeyEvent.VK_A){application.selectAllNodes();}
	    else if (code==KeyEvent.VK_G){application.getGlobalView();}
	    else if (code==KeyEvent.VK_B){application.moveBack();}
	    else if (code==KeyEvent.VK_E){application.showErrorMessages();}
	    else if (code==KeyEvent.VK_N){application.promptReset();}
	    else if (code==KeyEvent.VK_O){application.openProject();}
	    else if (code==KeyEvent.VK_S){application.saveProject();}
	    else if (code==KeyEvent.VK_P){application.printRequest();}
	}	
    }

    public void Krelease(ViewPanel v,char c,int code,int mod){}

    public void viewActivated(View v){}

    public void viewDeactivated(View v){}

    public void viewIconified(View v){}

    public void viewDeiconified(View v){}

    public void viewClosing(View v){
	Editor.vsm.getView(Editor.radarView).destroyView();
	Editor.rView=null;
    }

}
