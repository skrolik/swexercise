# Prerequisites, my environment is:
Windows 10 64bit
Apache Maven 3.5.2
1.8.0_151 – please use 1.8 
I’ve used Adobe Experience Manager (6.3.2.0)

# Build:
Maven configuration and repository set as described in this tutorial:
https://helpx.adobe.com/experience-manager/using/maven_arch12.html#CreateanAEMMaven12archetypeproject
In project directory run mvn clean install -PautoInstallPackage
That will deploy application and its demo content on http://localhost:4502 with user admin:admin

# App description
Main app form is available @ http://localhost:4502/content/swexercise/en/insurance-calculator.html - for different configurations please change parent pom of project
/content/swexercise/en/configuration/age-ranges – dictionary for age ranges – provides dictionary for select field @ rates configuration screen
/content/swexercise/en/configuration/zip-code-ranges – dictionary for zip code ranges – provides dictionary for select field @ rates configuration screen
/content/swexercise/en/configuration/rates-configuration – allows to configure rates for each of ranges and genders
Those screens aren’t validated, but until removed or cleared everything should work
Code is in org.saweko.swexercise.core package. Most classes have Javadoc or are self-explanatory.

# Possible improvements
Maybe rethink way that configuration is performed – current way int too efficient for this amount of data. 
Better and custom FE validation of form
 

