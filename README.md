TrackMyPet - Server part
==

About
--

TrackMyPet is a proof of concept of a Sigfox enabled GPS tracked intended to show the last known position of your equiped pet (OK, a big pet, like a pony or a cow; don't try with your hamster).

TrackMyPet is based on the following technologies : 

* GPS Tracker : Ardunio board with [Sigfox modem](http://sigfox.com/) (named [Akeru](http://akeru.cc/), homemade battery shield and MTK3339 GPS module.
* Play! 2.2 application hosted on [Heroku](http://herokuapp.com/) Server and [Kinvey](http://kinvey.com/) datastore for GPS data persistence.


GPS Tracker
--

Head over to the related [GitHub repository](https://github.com/Ekito/hackerz_akeru)!


Server part
--

The server part provides a map with a pin on the last know position of the GPS tracker. It's made of three main parts:

* A Kinvey datastore to store GPS coordinates sent by the tracker. You should create a datastore collection named “hackerz” to store these data, or will have to adapt server code to use the new name.
* A Play! 2.2 application. To fit it to your needs, you'll have to declare two system variable: “APP_KEY” which will contain you Kinvey datastore APP_KEY (seriously), and “MASTER_KEY” which will contain you Kinvey datastore MASTER_KEY (here am I, captain obvious). If you changed datastore collection name, you'll have to edit `app/controllers/Sigfox.java` file and replace “hackerz” by the new name in “dataStoreURL” variable.
* An account on [actoboard.com](http://actoboard.com), which is the service which receive Sigfox messages, and will redirect them to your Play! application. You'll have to create a new Akeru data source with the following settings:
	* Name: everything you want
	* Modem number: the four characters device ID visible on a sticker on the metal casing of the Sigfox modem
	* PAC: the id you got when you bought your Akeru board
	* Data format: latitude::float:32 longitude::float:32
	* Forwarding URL: the URL of your installation of the Play! application

If you want a cheap hosting for the application, head over [Heroku](http://herokuapp.com).

You can see “live action” [here](http://hackerz-server.herokuapp.com/)


Arduino support
--

Head over to the related [GitHub repository](https://github.com/Ekito/hackerz_3d)!