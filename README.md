301 Project
===========


Installation
-----------

- Download and install IntelliJ. Community Edition is fine, but we can get the Ultimate Edition for free
- To import the project:  
    1) Press "Import Project"  
    2) Go to the directory where repository was downloaded, and hit ok.  
    3) Select "Create Project From Existing sources", then hit next.  
    4) Decide a good Project Name, then hit next.  
    5) All source file directories should be selected, and hit next.  
    6) Press next to accept libraries, then next to accept module structure.  
    7) You will now need to import both the Java API (in folder jdkX.X.XX) and whatever android API's you want to build for (folder sdk).  
    8) Select the Android API you want to build for, then hit next.  
    9) Finally, ensure that the import project says "Android", which has a child "AndroidManifest.xml" (both should be checked), then hit finish.  
    10) Once IntelliJ opens up the project, select view ->Tool windows -> project to view all the code and resources in the project.  
    11) Then, make sure you have junit in the class path: Open the directory test/com.cmput301.cs.project.project/models.ExpenseTest.java (in IntelliJ). Select one of the erroring @Test, click alt-enter, and then select "add junit to classpath". If the option to add junit doesn't show up, try opening another test class, and do the same thing.

To set up running the app for the first time:  
1) Click run -> edit configurations -> click the +  
2) Select android application  
3) Change the module to the name of this project, and give the run configuration a good name  
4) Press ok.  

Then, all you will need to do in the future is press run->run '\<run config name\>'

Reference
-------
- https://android.googlesource.com/platform/developers/samples/android/+/master/ui/actionbar/DoneBar

License
-------
Copyright 2015  
Jordan Benson <jbenson@ualberta.ca>,  
Edmond Chui <echui@ualberta.ca>,  
William Kwan <wkwan@ualberta.ca>,  
Blaine Lewis <blaine1@ualberta.ca>,  
Morgan Redshaw <mredshaw@ualberta.ca>,  
Charles Rozsa <rozsa@ualberta.ca>  

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
