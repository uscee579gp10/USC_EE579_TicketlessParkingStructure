USC_EE579_TicketlessParkingStructure
====================================

Our ticketless parking system is to implement low-power Tmote Sky in the parking structure to detect parking space utilization. Users can use our android mobile app to register and reserver a parking spot prior to his or her arrival. It's energy-saving and time-saving.

System Features:

-Mobile device app to register and reserve parking space

-Automatic parking space detection

-Parking information tracking


Detail:

1. All the scripts in "Android App" and "MyServer" directories are main scripts as Client and Server prog.

  -MyServer:
  
      MulFunServer.java : Script with the main function
    
      MyServer.java : Contains Server side Algorithm and main socket establishing
    
      ClientHandler.java : Script support multi-user socket. The child socket process.
    
  -Android App: Contains the user app script

2. Files including "MyServer", "temp1.txt" and "Oscilloscope.java" should put under opt->tinyos-2.1.1->apps->Oscilloscope->java

  -Oscilloscope.java: get light strength from motes and write into temo1.txt

3. File "DemoSensorC.nc" and the directory "chips" should be updated under opt->tinyos-2.1.1->tos->platforms->telosb

  -DemoSensorC.nc : set the light sensor module as our interface
