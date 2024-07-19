package b4a.example;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class starter extends android.app.Service{
	public static class starter_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
            BA.LogInfo("** Receiver (starter) OnReceive **");
			android.content.Intent in = new android.content.Intent(context, starter.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, true, BA.class);
		}

	}
    static starter mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return starter.class;
	}
	@Override
	public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "b4a.example", "b4a.example.starter");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.starter", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!true && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, false) == false) {
				
		}
		else {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (starter) Create ***");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (true) {
			if (ServiceHelper.StarterHelper.runWaitForLayouts() == false) {
                BA.LogInfo("stopping spontaneous created service");
                stopSelf();
            }
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                handleStart(intent);
            }}))
			;
		else {
			ServiceHelper.StarterHelper.addWaitForLayout (new Runnable() {
				public void run() {
                    processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (starter) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
                    ServiceHelper.StarterHelper.removeWaitForLayout();
				}
			});
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    public void onTaskRemoved(android.content.Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (true)
            processBA.raiseEvent(null, "service_taskremoved");
            
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (starter) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = ServiceHelper.StarterHelper.handleStartIntent(intent, _service, processBA);
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }
	
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (true) {
            BA.LogInfo("** Service (starter) Destroy (ignored)**");
        }
        else {
            BA.LogInfo("** Service (starter) Destroy **");
		    processBA.raiseEvent(null, "service_destroy");
            processBA.service = null;
		    mostCurrent = null;
		    processBA.setActivityPaused(true);
            processBA.runHook("ondestroy", this, null);
        }
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.BleManager2 _manager = null;
public static String _currentstatetext = "";
public static int _currentstate = 0;
public static boolean _connected = false;
public static String _connectedname = "";
public static anywheresoftware.b4a.objects.collections.List _connectedservices = null;
public static anywheresoftware.b4a.objects.collections.Map _founddevices = null;
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static String _gpio_sid = "";
public static String _gpio_ctic = "";
public b4a.example.dateutils _dateutils = null;
public b4a.example.main _main = null;
public b4a.example.b4xcollections _b4xcollections = null;
public b4a.example.xuiviewsutils _xuiviewsutils = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 115;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 116;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 117;BA.debugLine="End Sub";
return false;
}
public static String  _connect(String _id) throws Exception{
 //BA.debugLineNum = 73;BA.debugLine="Sub Connect(Id As String)";
 //BA.debugLineNum = 74;BA.debugLine="manager.Connect2(Id, False) 'disabling auto conne";
_manager.Connect2(_id,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 76;BA.debugLine="End Sub";
return "";
}
public static String  _disconnect() throws Exception{
 //BA.debugLineNum = 35;BA.debugLine="Public Sub Disconnect";
 //BA.debugLineNum = 36;BA.debugLine="manager.Disconnect";
_manager.Disconnect();
 //BA.debugLineNum = 37;BA.debugLine="Manager_Disconnected";
_manager_disconnected();
 //BA.debugLineNum = 38;BA.debugLine="End Sub";
return "";
}
public static String  _manager_connected(anywheresoftware.b4a.objects.collections.List _services) throws Exception{
String _s = "";
 //BA.debugLineNum = 90;BA.debugLine="Sub Manager_Connected (services As List)";
 //BA.debugLineNum = 91;BA.debugLine="Log(\"Connected\")";
anywheresoftware.b4a.keywords.Common.LogImpl("11507329","Connected",0);
 //BA.debugLineNum = 92;BA.debugLine="connected = True";
_connected = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 93;BA.debugLine="ConnectedServices = services";
_connectedservices = _services;
 //BA.debugLineNum = 94;BA.debugLine="manager.SetNotify(gpio_sid,gpio_ctic,True)";
_manager.SetNotify(_gpio_sid,_gpio_ctic,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 95;BA.debugLine="CallSub2(Main, \"SetState\",connected)";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._main.getObject()),"SetState",(Object)(_connected));
 //BA.debugLineNum = 96;BA.debugLine="For Each s As String In ConnectedServices";
{
final anywheresoftware.b4a.BA.IterableList group6 = _connectedservices;
final int groupLen6 = group6.getSize()
;int index6 = 0;
;
for (; index6 < groupLen6;index6++){
_s = BA.ObjectToString(group6.Get(index6));
 //BA.debugLineNum = 97;BA.debugLine="manager.ReadData(s)";
_manager.ReadData(_s);
 //BA.debugLineNum = 98;BA.debugLine="Log(\"UUID: \"&s)";
anywheresoftware.b4a.keywords.Common.LogImpl("11507336","UUID: "+_s,0);
 }
};
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _manager_dataavailable(String _serviceid,anywheresoftware.b4a.objects.collections.Map _characteristics) throws Exception{
 //BA.debugLineNum = 78;BA.debugLine="Sub Manager_DataAvailable (ServiceId As String, Ch";
 //BA.debugLineNum = 79;BA.debugLine="If ServiceId = gpio_sid Then";
if ((_serviceid).equals(_gpio_sid)) { 
 //BA.debugLineNum = 80;BA.debugLine="CallSub3(Main, \"DataAvailable\", ServiceId, Chara";
anywheresoftware.b4a.keywords.Common.CallSubNew3(processBA,(Object)(mostCurrent._main.getObject()),"DataAvailable",(Object)(_serviceid),(Object)(_characteristics));
 };
 //BA.debugLineNum = 82;BA.debugLine="End Sub";
return "";
}
public static String  _manager_devicefound(String _name,String _id,anywheresoftware.b4a.objects.collections.Map _advertisingdata,double _rssi) throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub Manager_DeviceFound (Name As String, Id As Str";
 //BA.debugLineNum = 54;BA.debugLine="Log($\"Device found: ${Name}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("11179649",("Device found: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_name))+""),0);
 //BA.debugLineNum = 55;BA.debugLine="If Name.StartsWith(\"pi5a\") Then";
if (_name.startsWith("pi5a")) { 
 //BA.debugLineNum = 56;BA.debugLine="FoundDevices.Put(Name, Id)";
_founddevices.Put((Object)(_name),(Object)(_id));
 };
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _manager_disconnected() throws Exception{
 //BA.debugLineNum = 84;BA.debugLine="Sub Manager_Disconnected";
 //BA.debugLineNum = 85;BA.debugLine="Log(\"Disconnected\")";
anywheresoftware.b4a.keywords.Common.LogImpl("11441793","Disconnected",0);
 //BA.debugLineNum = 86;BA.debugLine="connected = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 87;BA.debugLine="CallSub2(Main, \"SetState\",connected)";
anywheresoftware.b4a.keywords.Common.CallSubNew2(processBA,(Object)(mostCurrent._main.getObject()),"SetState",(Object)(_connected));
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _manager_statechanged(int _state) throws Exception{
 //BA.debugLineNum = 40;BA.debugLine="Sub Manager_StateChanged (State As Int)";
 //BA.debugLineNum = 41;BA.debugLine="Select State";
switch (BA.switchObjectToInt(_state,_manager.STATE_POWERED_OFF,_manager.STATE_POWERED_ON,_manager.STATE_UNSUPPORTED)) {
case 0: {
 //BA.debugLineNum = 43;BA.debugLine="currentStateText = \"POWERED OFF\"";
_currentstatetext = "POWERED OFF";
 break; }
case 1: {
 //BA.debugLineNum = 45;BA.debugLine="currentStateText = \"POWERED ON\"";
_currentstatetext = "POWERED ON";
 break; }
case 2: {
 //BA.debugLineNum = 47;BA.debugLine="currentStateText = \"UNSUPPORTED\"";
_currentstatetext = "UNSUPPORTED";
 break; }
}
;
 //BA.debugLineNum = 49;BA.debugLine="currentState = State";
_currentstate = _state;
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Public manager As BleManager2";
_manager = new anywheresoftware.b4a.objects.BleManager2();
 //BA.debugLineNum = 10;BA.debugLine="Public currentStateText As String = \"UNKNOWN\"";
_currentstatetext = "UNKNOWN";
 //BA.debugLineNum = 11;BA.debugLine="Public currentState As Int";
_currentstate = 0;
 //BA.debugLineNum = 12;BA.debugLine="Public connected As Boolean = False";
_connected = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 13;BA.debugLine="Public ConnectedName As String";
_connectedname = "";
 //BA.debugLineNum = 14;BA.debugLine="Private ConnectedServices As List";
_connectedservices = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 15;BA.debugLine="Public FoundDevices As Map";
_founddevices = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 16;BA.debugLine="Public rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 18;BA.debugLine="Dim gpio_sid As String =	\"6b5ed4bd-a283-4bf5-bec5";
_gpio_sid = "6b5ed4bd-a283-4bf5-bec5-0c9275ef85f9";
 //BA.debugLineNum = 19;BA.debugLine="Dim gpio_ctic As String =	\"0000abcd-0000-1000-800";
_gpio_ctic = "0000abcd-0000-1000-8000-00805f9b34fb";
 //BA.debugLineNum = 21;BA.debugLine="End Sub";
return "";
}
public static String  _readdata() throws Exception{
String _s = "";
 //BA.debugLineNum = 29;BA.debugLine="Public Sub ReadData";
 //BA.debugLineNum = 30;BA.debugLine="For Each s As String In ConnectedServices";
{
final anywheresoftware.b4a.BA.IterableList group1 = _connectedservices;
final int groupLen1 = group1.getSize()
;int index1 = 0;
;
for (; index1 < groupLen1;index1++){
_s = BA.ObjectToString(group1.Get(index1));
 //BA.debugLineNum = 31;BA.debugLine="manager.ReadData(s)";
_manager.ReadData(_s);
 }
};
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static void  _scan() throws Exception{
ResumableSub_Scan rsub = new ResumableSub_Scan(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_Scan extends BA.ResumableSub {
public ResumableSub_Scan(b4a.example.starter parent) {
this.parent = parent;
}
b4a.example.starter parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 65;BA.debugLine="FoundDevices.Initialize 'process global Map varia";
parent._founddevices.Initialize();
 //BA.debugLineNum = 66;BA.debugLine="manager.Scan2(Null, False)";
parent._manager.Scan2((anywheresoftware.b4a.objects.collections.List) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.collections.List(), (java.util.List)(anywheresoftware.b4a.keywords.Common.Null)),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 67;BA.debugLine="Sleep(5000)";
anywheresoftware.b4a.keywords.Common.Sleep(processBA,this,(int) (5000));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
 //BA.debugLineNum = 68;BA.debugLine="manager.StopScan";
parent._manager.StopScan();
 //BA.debugLineNum = 69;BA.debugLine="CallSubDelayed2(Main, \"AfterScan\", FoundDevices)";
anywheresoftware.b4a.keywords.Common.CallSubDelayed2(processBA,(Object)(parent.mostCurrent._main.getObject()),"AfterScan",(Object)(parent._founddevices));
 //BA.debugLineNum = 71;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 23;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 26;BA.debugLine="manager.Initialize(\"manager\")";
_manager.Initialize(processBA,"manager");
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 119;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 121;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 106;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 107;BA.debugLine="Service.StopAutomaticForeground 'Starter service";
mostCurrent._service.StopAutomaticForeground();
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _service_taskremoved() throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub Service_TaskRemoved";
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
}
