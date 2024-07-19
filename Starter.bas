B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=9.9
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
	#ExcludeFromLibrary: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Public manager As BleManager2
	Public currentStateText As String = "UNKNOWN"
	Public currentState As Int
	Public connected As Boolean = False
	Public ConnectedName As String
	Private ConnectedServices As List
	Public FoundDevices As Map
	Public rp As RuntimePermissions
	
	Dim gpio_sid As String =	"6b5ed4bd-a283-4bf5-bec5-0c9275ef85f9"
	Dim gpio_ctic As String =	"0000abcd-0000-1000-8000-00805f9b34fb"

End Sub

Sub Service_Create
	'This is the program entry point.
	'This is a good place to load resources that are not specific to a single activity.
	manager.Initialize("manager")

End Sub
Public Sub ReadData
	For Each s As String In ConnectedServices
		manager.ReadData(s)
	Next
End Sub

Public Sub Disconnect
	manager.Disconnect
	Manager_Disconnected
End Sub

Sub Manager_StateChanged (State As Int)
	Select State
		Case manager.STATE_POWERED_OFF
			currentStateText = "POWERED OFF"
		Case manager.STATE_POWERED_ON
			currentStateText = "POWERED ON"
		Case manager.STATE_UNSUPPORTED
			currentStateText = "UNSUPPORTED"
	End Select
	currentState = State
	'CallSub(Main, "StateChanged")
End Sub

Sub Manager_DeviceFound (Name As String, Id As String, AdvertisingData As Map, RSSI As Double)
	Log($"Device found: ${Name}"$)
	If Name.StartsWith("pi5a") Then
		FoundDevices.Put(Name, Id)
	End If
End Sub


Sub Scan
	'If rp.Check(rp.PERMISSION_ACCESS_FINE_LOCATION) = False Then
	'	Log("No location permission.")
	'Else
	FoundDevices.Initialize 'process global Map variable
	manager.Scan2(Null, False)
	Sleep(5000)
	manager.StopScan
	CallSubDelayed2(Main, "AfterScan", FoundDevices)
	'End If
End Sub

Sub Connect(Id As String)
	manager.Connect2(Id, False) 'disabling auto connect can make the connection quicker
	'manager.Connect(Id)
End Sub

Sub Manager_DataAvailable (ServiceId As String, Characteristics As Map)
	If ServiceId = gpio_sid Then
		CallSub3(Main, "DataAvailable", ServiceId, Characteristics)
	End If
End Sub

Sub Manager_Disconnected
	Log("Disconnected")
	connected = False
	CallSub2(Main, "SetState",connected)
End Sub

Sub Manager_Connected (services As List)
	Log("Connected")
	connected = True
	ConnectedServices = services
	manager.SetNotify(gpio_sid,gpio_ctic,True)
	CallSub2(Main, "SetState",connected)
	For Each s As String In ConnectedServices
		manager.ReadData(s)
		Log("UUID: "&s)
	Next
	'Main.cticData(0) = 7
	'Main.cticData(1) = 0
	'manager.WriteData(s,gpio_ctic,Main.cticData)

End Sub

Sub Service_Start (StartingIntent As Intent)
	Service.StopAutomaticForeground 'Starter service can start in the foreground state in some edge cases.
End Sub

Sub Service_TaskRemoved
	'This event will be raised when the user removes the app from the recent apps list.
End Sub

'Return true to allow the OS default exceptions handler to handle the uncaught exception.
Sub Application_Error (Error As Exception, StackTrace As String) As Boolean
	Return True
End Sub

Sub Service_Destroy

End Sub
