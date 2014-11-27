Build A Mobile App Leveraging Watson Question and Answer Service on Bluemix

Summary:
May we have the chance to embed IBM Watson features into apps? The answer is yes by simply using IBM Watson services on Bluemix. In this sample, we build up a mobile app called "TravelBuddy" which is to answer travel relevant questions while using the Watson Question and Answer service.

One User inputs some question on the mobile app "TravelBuddy", and then he will get the response from Watson service. The app is used to show how easy to embed Watson feature into a mobile app with only a few source code lines.

Outline:
I. Introduction
	a.  App URL: 
	b.  Code URL: https://github.com/chunbintang/travelbuddy
II. Before getting started
	a.  Bluemix Account
	b.  Java Liberty and Watson Question And Answer Service Skill
	c.  Android Development Skill
III. How to create the app on Bluemix
	a.  Java Liberty Runtime
        1. Open the Catalog menu.
		2. From the Runtimes section, click Liberty for Java.
        3. In the App field, specify the name of your app, in this case, it is set to qa002.
        4. Click Create.Wait for your application to provision.
　　b.  Watson Question And Answer Service
        1. Click the App created in the Dashboard. Open the Catalog menu.
		2. Click Add A Service.
        3. Choose Question And Answer Service under Watson.
		4. Click Create. Click OK if it is prompted to restart the application.
IV. Build and Run
	a.  Server Side
        1. Build the project using Eclipse and export it as war archive, for example, "travelbuddy.war". You may find a compiled one under MyData/ under source code in case you do not want to compile it by yourself.
		2. Use cf tools to deploy app.
cf push <app_name> -p travelbuddy.war
	b.  Mobile Client Side
        1. Set up the Android IDE using Eclipse.
		2. Clone the Android package from  /TravelBuddyAndroid, import it into Android IDE .
		3. Modify com.ibm.sample.travelbuddy.HttpUtil.java to your app name.
　　	private final static String MOBILE_BACKEND_URL = "http://<app_name>.mybluemix.net/MobileServlet";
　　In my case, ad the app_name is qa002, it is modified to:
　　private final static String MOBILE_BACKEND_URL = "http://qa002.mybluemix.net/MobileServlet";
　　c. Build Android app.
V. Using the existing Android App if you do not have the Android environment 
        1. Go to https://github.com/chunbintang/travelbuddy/tree/master/TravelBuddyAndroid, and download the TravelBuddyAndroid.apk file, it will point to my app qa002.mybluemix.net.
		2. Install it on a Android phone or tablet later than Android 4.0.
		3. Input a question into the text field, for example, "where is new york"
		4. Click Search to get the answer to the question above.