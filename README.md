# DCF-Coursework
A few test classes along with the custom provider its self

## Current Test Cases

* src/main/java/uk/ac/ljmu/fet/cs/group20/tests/  [TestForLessReliableMachines.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/tests/TestForLessReliableMachines.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/tests/  [TestForLoadDependentPrice.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/tests/TestForLoadDependentPrice.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/tests/  [TestForProviderReduction.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/tests/TestForProviderReduction.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/tests/  [TestRCAware.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/tests/TestRCAware.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/tests/  [TestReactive.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/tests/TestReactive.java?at=master&fileviewer=file-view-default)

### Test Solutions include

* src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/  [LoadDependentProvider.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/LoadDependentProvider.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/  [MyFirstProvider.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/MyFirstProvider.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/  [MyLessReliableProvider.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/MyLessReliableProvider.java?at=master&fileviewer=file-view-default)
* src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/  [MyRCAwareProvider.java](https://bitbucket.org/group20-/dcf-coursework/src/b9587bb223f754b6dff53cee2747b18f1cca8569/src/main/java/uk/ac/ljmu/fet/cs/group20/testsolution/MyRCAwareProvider.java?at=master&fileviewer=file-view-default)

#### Remark
The solutions above are kept to be as simplistic as possible in order to allow easy understanding of the mechanics in place. As a result, some of the solutions do not offer 100% success rate in the test runs. Any problems email Mark, he's the only one who seems to know what he's doing. ;-D

If you want to run the test, make sure to change the VM argument of the test case so that it knows which java class to look for. You can do this by clicking 'Run Configurations' and then going to the arguments tab. You will then see a text box with VM arguments above it. The vm argument you should state will look something like this: 

-Dhu.unimiskolc.iit.distsys.CustomCloudProvider=uk.ac.ljmu.fet.cs.group20.testsolutions.MyFirstProvider 

This is the vm argument for the TestForProviderReduction. Just modify this vm argument in case you want to test the other test cases but make sure you specify the correct file or it will fail.