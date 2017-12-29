**Instrumentation of Apache Commons Lang**
 
The application ast-instrumentation is an instrumenting app that takes the source code of another app as input and creates an AST using eclipse JDT. The AST is then modified to add instrumentation code. 

How to run the instrumenting application:
Once the instrumenting application is setup in the IDE open Main.java.

The main class consists of the following statement. Make changes to source_dir(source code directory) and backup_dir(Backup directory) in the statement.
    InstrumentationParser instrumentationParser = new InstrumentationParser(source_dir, backup_dir);

The execution of the main class parses the entire source application and adds instrumentation statements.

Running Unit test cases of the instrumenting application:

Execute the following gradle command : 
    gradle test


The TemplateClass.java and PairClass.java, the two files used to add instrumentation statements need to be added to the Java application. 
I have currently added the jar of the instrumenting app to the build path of the Java application.





**How to run the instrumented application:**

Application : Apache Commons Lang is a library containing Java utility classes that provide helper utilities for the java.lang API.

**Implementation of Junit test cases details:**

1. Execute the instrumented application's test cases using :

    gradle test
    
** Output:**
    
The trace(output) of the app is written to a file. The cofiguration of this file is given in log4j.properties. Currently the file path is set to :
        log4j.appender.file.File=D:\\application.log (check this file to see the output of the instrumenting statements)
        
        
        
	
	
**Implementation of build scripts:**

1. Maven : Was already present in the code.

2. Gradle : The build.gradle and settings.gradle files in the root directory are used
for running gradle tasks on the project.

   **Performing Tests:**
   
        gradle clean
        
        gradle test
	
   **Compiling classes:**
   
        gradle assemble
    
   **Creating build:**
    
        gradle build
      

3. SBT : The build.sbt file in the root directory is used to perform SBT build on the project.

    **Performing Tests:**
       
            sbt clean
            
            sbt test
    	
    **Compiling classes:**
       
            sbt compile
        
    **Creating build:**
        
            sbt package
        
4. **Running the project using jar file:**

    Once the jar file is ready after using the above build tools we will run the utility.
    Since this is a utility containing helper methods we will call them from a driver program. 
    For this we will create a new Java Project and create the following drive program in it. 
    Also we will include the Apache Commons Lang jar in the new Java project.
    
        import org.apache.commons.lang3.StringUtils;
        public class ApacheCommonsDriver {
        
            public static void main(String[] args) throws InterruptedException {
        
                for(int i =0; i< 20;i++) {
                    System.out.println("This is iteration "+i);
                    Thread.sleep(1000);
                }
        
                System.out.println(StringUtils.isPalindrome("stats"));
                System.out.println(StringUtils.hasWhitespace("OOLE  "));
                System.out.println(StringUtils.getCountVowelsInString("Adarsh"));
            }
        }


    If we see the above class I have added code to delay the termination of the program. 
    This is done to be able to monitor the app properly which we will see in the following section.
    In the above class ApacheCommonsDriver.java I have called the utility methods that I had created in the project.
    The above class can be executed directly.
    

    
            
            
         
     
    
    


