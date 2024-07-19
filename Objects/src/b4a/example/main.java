package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _xui = null;
public static byte[] _bledatain = null;
public static byte[] _cticdata = null;
public static anywheresoftware.b4a.keywords.StringBuilderWrapper _sb = null;
public static anywheresoftware.b4a.objects.Timer _temptimer = null;
public b4a.example.anotherprogressbar _anotherprogressbar1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnconnected = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btndisconnected = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnscan = null;
public anywheresoftware.b4a.objects.LabelWrapper _lblstatus = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltemp = null;
public anywheresoftware.b4a.objects.ButtonWrapper _btnled = null;
public b4a.example.dateutils _dateutils = null;
public b4a.example.starter _starter = null;
public b4a.example.b4xcollections _b4xcollections = null;
public b4a.example.xuiviewsutils _xuiviewsutils = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 36;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 37;BA.debugLine="Activity.LoadLayout(\"Layout\")";
mostCurrent._activity.LoadLayout("Layout",mostCurrent.activityBA);
 //BA.debugLineNum = 38;BA.debugLine="temptimer.Initialize(\"temptimer\",5000)";
_temptimer.Initialize(processBA,"temptimer",(long) (5000));
 //BA.debugLineNum = 39;BA.debugLine="btnScan.Enabled = True";
mostCurrent._btnscan.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 40;BA.debugLine="btnScan.Visible = True";
mostCurrent._btnscan.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 41;BA.debugLine="btnConnected.Visible = False";
mostCurrent._btnconnected.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 42;BA.debugLine="btnConnected.Enabled = False";
mostCurrent._btnconnected.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 43;BA.debugLine="temptimer.Enabled = False";
_temptimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 138;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 134;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 136;BA.debugLine="End Sub";
return "";
}
public static void  _afterscan(anywheresoftware.b4a.objects.collections.Map _devices) throws Exception{
ResumableSub_AfterScan rsub = new ResumableSub_AfterScan(null,_devices);
rsub.resume(processBA, null);
}
public static class ResumableSub_AfterScan extends BA.ResumableSub {
public ResumableSub_AfterScan(b4a.example.main parent,anywheresoftware.b4a.objects.collections.Map _devices) {
this.parent = parent;
this._devices = _devices;
}
b4a.example.main parent;
anywheresoftware.b4a.objects.collections.Map _devices;
anywheresoftware.b4a.objects.collections.List _items = null;
String _name = "";
int _index = 0;
String _id = "";
anywheresoftware.b4a.BA.IterableList group3;
int index3;
int groupLen3;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 56;BA.debugLine="Dim items As List";
_items = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 57;BA.debugLine="items.Initialize";
_items.Initialize();
 //BA.debugLineNum = 58;BA.debugLine="For Each name As String In Devices.Keys";
if (true) break;

case 1:
//for
this.state = 4;
group3 = _devices.Keys();
index3 = 0;
groupLen3 = group3.getSize();
this.state = 14;
if (true) break;

case 14:
//C
this.state = 4;
if (index3 < groupLen3) {
this.state = 3;
_name = BA.ObjectToString(group3.Get(index3));}
if (true) break;

case 15:
//C
this.state = 14;
index3++;
if (true) break;

case 3:
//C
this.state = 15;
 //BA.debugLineNum = 59;BA.debugLine="items.Add(name)";
_items.Add((Object)(_name));
 if (true) break;
if (true) break;
;
 //BA.debugLineNum = 61;BA.debugLine="If items.Size > 0 Then";

case 4:
//if
this.state = 13;
if (_items.getSize()>0) { 
this.state = 6;
}else {
this.state = 12;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 62;BA.debugLine="InputListAsync(items, \"Select Device\", 0, False)";
anywheresoftware.b4a.keywords.Common.InputListAsync(_items,BA.ObjectToCharSequence("Select Device"),(int) (0),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 63;BA.debugLine="Wait For InputList_Result (Index As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("inputlist_result", processBA, this, null);
this.state = 16;
return;
case 16:
//C
this.state = 7;
_index = (Integer) result[0];
;
 //BA.debugLineNum = 64;BA.debugLine="If Index <> DialogResponse.CANCEL Then";
if (true) break;

case 7:
//if
this.state = 10;
if (_index!=anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 65;BA.debugLine="Dim id As String = Devices.Get(items.Get(Index)";
_id = BA.ObjectToString(_devices.Get(_items.Get(_index)));
 //BA.debugLineNum = 66;BA.debugLine="Log(\"Connect to: \" & id)";
anywheresoftware.b4a.keywords.Common.LogImpl("1262155","Connect to: "+_id,0);
 //BA.debugLineNum = 67;BA.debugLine="Starter.Connect(id)";
parent.mostCurrent._starter._connect /*String*/ (_id);
 //BA.debugLineNum = 68;BA.debugLine="AnotherProgressBar1.Visible = False";
parent.mostCurrent._anotherprogressbar1._setvisible /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 69;BA.debugLine="lblstatus.TextColor = xui.Color_Green";
parent.mostCurrent._lblstatus.setTextColor(parent._xui.Color_Green);
 //BA.debugLineNum = 70;BA.debugLine="lblstatus.Text = \"Pi 5 CONNECTED\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence("Pi 5 CONNECTED"));
 //BA.debugLineNum = 71;BA.debugLine="lblstatus.Visible = True";
parent.mostCurrent._lblstatus.setVisible(anywheresoftware.b4a.keywords.Common.True);
 if (true) break;

case 10:
//C
this.state = 13;
;
 if (true) break;

case 12:
//C
this.state = 13;
 //BA.debugLineNum = 74;BA.debugLine="btnDisconnected.Visible = False";
parent.mostCurrent._btndisconnected.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 75;BA.debugLine="btnScan.Visible = True";
parent.mostCurrent._btnscan.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 76;BA.debugLine="AnotherProgressBar1.Visible = False";
parent.mostCurrent._anotherprogressbar1._setvisible /*boolean*/ (anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 77;BA.debugLine="lblstatus.TextColor = xui.Color_Red";
parent.mostCurrent._lblstatus.setTextColor(parent._xui.Color_Red);
 //BA.debugLineNum = 78;BA.debugLine="lblstatus.Visible = True";
parent.mostCurrent._lblstatus.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 79;BA.debugLine="lblstatus.Text = \" Pi 5 NOT FOUND\"";
parent.mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(" Pi 5 NOT FOUND"));
 if (true) break;

case 13:
//C
this.state = -1;
;
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _inputlist_result(int _index) throws Exception{
}
public static String  _btnconnected_click() throws Exception{
 //BA.debugLineNum = 170;BA.debugLine="Private Sub btnConnected_Click";
 //BA.debugLineNum = 171;BA.debugLine="temptimer.Enabled = False";
_temptimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 172;BA.debugLine="lbltemp.Text = \"\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 173;BA.debugLine="lblstatus.Text = \"\"";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 174;BA.debugLine="Starter.Disconnect";
mostCurrent._starter._disconnect /*String*/ ();
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _btndisconnected_click() throws Exception{
 //BA.debugLineNum = 166;BA.debugLine="Private Sub btnDisconnected_Click";
 //BA.debugLineNum = 168;BA.debugLine="End Sub";
return "";
}
public static String  _btnled_click() throws Exception{
 //BA.debugLineNum = 178;BA.debugLine="Private Sub btnLED_Click";
 //BA.debugLineNum = 179;BA.debugLine="cticData(0) = 0x55";
_cticdata[(int) (0)] = (byte) (((int)0x55));
 //BA.debugLineNum = 180;BA.debugLine="Starter.manager.WriteData(Starter.gpio_sid,Starte";
mostCurrent._starter._manager /*anywheresoftware.b4a.objects.BleManager2*/ .WriteData(mostCurrent._starter._gpio_sid /*String*/ ,mostCurrent._starter._gpio_ctic /*String*/ ,_cticdata);
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static void  _btnscan_click() throws Exception{
ResumableSub_btnScan_Click rsub = new ResumableSub_btnScan_Click(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_btnScan_Click extends BA.ResumableSub {
public ResumableSub_btnScan_Click(b4a.example.main parent) {
this.parent = parent;
}
b4a.example.main parent;
anywheresoftware.b4a.objects.collections.List _permissions = null;
anywheresoftware.b4a.phone.Phone _phone = null;
String _per = "";
String _permission = "";
boolean _result = false;
anywheresoftware.b4a.BA.IterableList group12;
int index12;
int groupLen12;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 143;BA.debugLine="lblstatus.Visible = False";
parent.mostCurrent._lblstatus.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 144;BA.debugLine="AnotherProgressBar1.Visible = True";
parent.mostCurrent._anotherprogressbar1._setvisible /*boolean*/ (anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 145;BA.debugLine="btnDisconnected.Visible = True";
parent.mostCurrent._btndisconnected.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 146;BA.debugLine="btnScan.Visible = False";
parent.mostCurrent._btnscan.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 147;BA.debugLine="Dim Permissions As List";
_permissions = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 148;BA.debugLine="Dim phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 149;BA.debugLine="If phone.SdkVersion >= 31 Then";
if (true) break;

case 1:
//if
this.state = 6;
if (_phone.getSdkVersion()>=31) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 150;BA.debugLine="Permissions = Array(\"android.permission.BLUETOOT";
_permissions = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)("android.permission.BLUETOOTH_SCAN"),(Object)("android.permission.BLUETOOTH_CONNECT"),(Object)(parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .PERMISSION_ACCESS_FINE_LOCATION)});
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 152;BA.debugLine="Permissions = Array(Starter.rp.PERMISSION_ACCESS";
_permissions = anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .PERMISSION_ACCESS_FINE_LOCATION)});
 if (true) break;
;
 //BA.debugLineNum = 154;BA.debugLine="For Each per As String In Permissions";

case 6:
//for
this.state = 13;
group12 = _permissions;
index12 = 0;
groupLen12 = group12.getSize();
this.state = 14;
if (true) break;

case 14:
//C
this.state = 13;
if (index12 < groupLen12) {
this.state = 8;
_per = BA.ObjectToString(group12.Get(index12));}
if (true) break;

case 15:
//C
this.state = 14;
index12++;
if (true) break;

case 8:
//C
this.state = 9;
 //BA.debugLineNum = 155;BA.debugLine="Starter.rp.CheckAndRequest(per)";
parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .CheckAndRequest(processBA,_per);
 //BA.debugLineNum = 156;BA.debugLine="Wait For Activity_PermissionResult (Permission A";
anywheresoftware.b4a.keywords.Common.WaitFor("activity_permissionresult", processBA, this, null);
this.state = 16;
return;
case 16:
//C
this.state = 9;
_permission = (String) result[0];
_result = (Boolean) result[1];
;
 //BA.debugLineNum = 157;BA.debugLine="If Result = False Then";
if (true) break;

case 9:
//if
this.state = 12;
if (_result==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 11;
}if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 158;BA.debugLine="ToastMessageShow(\"No permission: \" & Permission";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No permission: "+_permission),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 159;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 12:
//C
this.state = 15;
;
 if (true) break;
if (true) break;

case 13:
//C
this.state = -1;
;
 //BA.debugLineNum = 163;BA.debugLine="Starter.Scan";
parent.mostCurrent._starter._scan /*void*/ ();
 //BA.debugLineNum = 164;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _activity_permissionresult(String _permission,boolean _result) throws Exception{
}
public static String  _dataavailable(String _service,anywheresoftware.b4a.objects.collections.Map _characteristics) throws Exception{
String _key = "";
 //BA.debugLineNum = 97;BA.debugLine="Sub DataAvailable (Service As String, Characterist";
 //BA.debugLineNum = 98;BA.debugLine="If Service = Starter.gpio_sid Then";
if ((_service).equals(mostCurrent._starter._gpio_sid /*String*/ )) { 
 //BA.debugLineNum = 99;BA.debugLine="For Each key As String In Characteristics.Keys";
{
final anywheresoftware.b4a.BA.IterableList group2 = _characteristics.Keys();
final int groupLen2 = group2.getSize()
;int index2 = 0;
;
for (; index2 < groupLen2;index2++){
_key = BA.ObjectToString(group2.Get(index2));
 //BA.debugLineNum = 100;BA.debugLine="If key = Starter.gpio_ctic Then";
if ((_key).equals(mostCurrent._starter._gpio_ctic /*String*/ )) { 
 //BA.debugLineNum = 101;BA.debugLine="bleDataIn = Characteristics.Get(key) 	'get max";
_bledatain = (byte[])(_characteristics.Get((Object)(_key)));
 //BA.debugLineNum = 102;BA.debugLine="If bleDataIn.Length > 0 Then			'check for vali";
if (_bledatain.length>0) { 
 //BA.debugLineNum = 103;BA.debugLine="Select bleDataIn(0)					'bleDataIn(0) = senso";
switch (BA.switchObjectToInt(_bledatain[(int) (0)],(byte) (((int)0x35)))) {
case 0: {
 //BA.debugLineNum = 105;BA.debugLine="Select bleDataIn.Length";
switch (BA.switchObjectToInt(_bledatain.length,(int) (5),(int) (4),(int) (3),(int) (2))) {
case 0: {
 //BA.debugLineNum = 107;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 108;BA.debugLine="lbltemp.Text = \"\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 109;BA.debugLine="lbltemp.Text = sb.Append(Chr(bleDataIn(1)";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(_sb.Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (1)])))).Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (2)])))).Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (3)])))).Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (4)])))).getObject()));
 break; }
case 1: {
 //BA.debugLineNum = 111;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 112;BA.debugLine="lbltemp.Text = \"\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 113;BA.debugLine="lbltemp.Text = sb.Append(Chr(bleDataIn(1)";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(_sb.Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (1)])))).Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (2)])))).Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (3)])))).getObject()));
 break; }
case 2: {
 //BA.debugLineNum = 115;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 116;BA.debugLine="lbltemp.Text = \"\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 117;BA.debugLine="lbltemp.Text = sb.Append(Chr(bleDataIn(1)";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(_sb.Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (1)])))).Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (2)])))).getObject()));
 break; }
case 3: {
 //BA.debugLineNum = 119;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 120;BA.debugLine="lbltemp.Text = \"\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 121;BA.debugLine="lbltemp.Text = sb.Append(Chr(bleDataIn(1)";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(_sb.Append(BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (_bledatain[(int) (1)])))).getObject()));
 break; }
default: {
 //BA.debugLineNum = 123;BA.debugLine="sb.Initialize";
_sb.Initialize();
 //BA.debugLineNum = 124;BA.debugLine="lbltemp.TextColor = xui.Color_Red";
mostCurrent._lbltemp.setTextColor(_xui.Color_Red);
 //BA.debugLineNum = 125;BA.debugLine="lbltemp.Text = \"\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 126;BA.debugLine="lbltemp.Text = \"ERROR\"";
mostCurrent._lbltemp.setText(BA.ObjectToCharSequence("ERROR"));
 break; }
}
;
 break; }
}
;
 };
 };
 }
};
 };
 //BA.debugLineNum = 133;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 25;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 27;BA.debugLine="Private AnotherProgressBar1 As AnotherProgressBar";
mostCurrent._anotherprogressbar1 = new b4a.example.anotherprogressbar();
 //BA.debugLineNum = 28;BA.debugLine="Private btnConnected As Button";
mostCurrent._btnconnected = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private btnDisconnected As Button";
mostCurrent._btndisconnected = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private btnScan As Button";
mostCurrent._btnscan = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private lblstatus As Label";
mostCurrent._lblstatus = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private lbltemp As Label";
mostCurrent._lbltemp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private btnLED As Button";
mostCurrent._btnled = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 34;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        b4a.example.dateutils._process_globals();
main._process_globals();
starter._process_globals();
b4xcollections._process_globals();
xuiviewsutils._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Private xui As XUI";
_xui = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 19;BA.debugLine="Dim bleDataIn() As Byte";
_bledatain = new byte[(int) (0)];
;
 //BA.debugLineNum = 20;BA.debugLine="Dim cticData(8) As Byte";
_cticdata = new byte[(int) (8)];
;
 //BA.debugLineNum = 21;BA.debugLine="Dim sb As StringBuilder";
_sb = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim temptimer As Timer";
_temptimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 23;BA.debugLine="End Sub";
return "";
}
public static String  _setstate(boolean _connected) throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Sub SetState (connected As Boolean)";
 //BA.debugLineNum = 86;BA.debugLine="btnScan.Enabled = Not(connected)";
mostCurrent._btnscan.setEnabled(anywheresoftware.b4a.keywords.Common.Not(_connected));
 //BA.debugLineNum = 87;BA.debugLine="btnScan.Visible = Not(connected)";
mostCurrent._btnscan.setVisible(anywheresoftware.b4a.keywords.Common.Not(_connected));
 //BA.debugLineNum = 88;BA.debugLine="btnConnected.Visible = connected";
mostCurrent._btnconnected.setVisible(_connected);
 //BA.debugLineNum = 89;BA.debugLine="btnConnected.Enabled = connected";
mostCurrent._btnconnected.setEnabled(_connected);
 //BA.debugLineNum = 91;BA.debugLine="If connected = False Then";
if (_connected==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 92;BA.debugLine="lblstatus.Visible = False";
mostCurrent._lblstatus.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 93;BA.debugLine="lblstatus.Text = \"\"";
mostCurrent._lblstatus.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return "";
}
public static String  _temptimer_tick() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Private Sub temptimer_Tick";
 //BA.debugLineNum = 48;BA.debugLine="If Starter.connected = True Then";
if (mostCurrent._starter._connected /*boolean*/ ==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 49;BA.debugLine="cticData(0) = 0x54";
_cticdata[(int) (0)] = (byte) (((int)0x54));
 //BA.debugLineNum = 50;BA.debugLine="cticData(1) = 0x00";
_cticdata[(int) (1)] = (byte) (((int)0x00));
 //BA.debugLineNum = 51;BA.debugLine="Starter.manager.WriteData(Starter.gpio_sid,Start";
mostCurrent._starter._manager /*anywheresoftware.b4a.objects.BleManager2*/ .WriteData(mostCurrent._starter._gpio_sid /*String*/ ,mostCurrent._starter._gpio_ctic /*String*/ ,_cticdata);
 };
 //BA.debugLineNum = 53;BA.debugLine="End Sub";
return "";
}
}
