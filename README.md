Java Config-Builder  [![Build Status](https://travis-ci.org/TNG/config-builder.png?branch=master)](https://travis-ci.org/TNG/config-builder)
==================

#### Table of Contents
[What It Is](#what-is-it)  
[Motivation](#motivation)    
[How To Build Your Config](#how-to-build-your-config)  
[How To Merge With An Existing Config](#how-to-merge-with-an-existing-config)  
[Usage example](#usage-example)  
[Java Doc](#java-doc)  

What It Is
----------

The ConfigBuilder makes use of annotations and reflections in order to build configured instances of custom classes. 

Its features include   
1. defining default values and loading of values from properties files, system properties, the command line and others  
2. configuring of not only String values, but fields of arbitrary types  
3. configuring of collection fields   
4. merging configs   
5. JSR303 validation of the instances it builds.  

Motivation
----------

Many Java Propjects include one or more classes that store configuration values and objects. Often, these come from
properties files, system properties and environment variables or command line arguments, which requires the developer
to implement the finding and loading of files, parsing the values etc. for every new project.

This is is a time-consuming process, so why not spare this time and get started much faster? Although there are libraries
that implement the loading of properties files and some possibilities of building configured objects e.g. in Spring, 
there hasn't been a really easy-yet-powerful solution so far.

This is where the Config Builder comes in. It doesn't require any additional classes besides the config itself. 
Instead of manually implementing the loading of values from files etc., building a config can now be easily done 
by using annotations.

How To Build Your Config
------------------------

####1. Create your class:
```java
public class Config {
    private String someNumber;
    private Collection<PidFix> stringCollection;
    ...
}
```
####2. Annotate the class (configure the loading of properties files)

If you want the ConfigBuilder to get values from properties files, 
you can specify the files' basenames (no file extension or path) by 
annotating your config class with the @PropertiesFiles annotation. 
You can specify multiple basenames like this: 
```java
@PropertiesFiles({file1,file2,...})
```

By default, properties files are loaded using the PropertyLoader's default config, which 
searches for files in the current directory, the ContextClassLoader and the user's home directory.
You can manually specify the search locations by annotating your config class with the @PropertyLocations annotation, e.g.
```java
@PropertyLocations(directories = {"/home/user"}, resourcesForClasses={MyApp.class}, contextClassLoader = true)
```

The PropertyLoader also searches for files with the default suffixes, i.e. the user name, local host names and 'override'.
You can manually set the suffixes by annotating your config class with the @PropertySuffixes annotation like this:
```java
@PropertySuffixes(extraSuffixes = {"tngtech","myname"}, hostNames = true)
```

The default file extensions are .properties and .xml. You can replace the .properties file extension with your own
by annotating your config class with 
```java
@PropertyExtension("fileextension")
```

####3. Annotate the fields

#####3.1 Get the String value
There are five annotations that specify where the String value that configures a field comes from:
```java
@DefaultValue("value")
@SystemPropertyValue("property.key")
@EnvironmentVariableValue("ENV_VAR")
@PropertyValue("property.key")
@CommandLineValue(shortOpt = "o", longOpt = "option", hasArg=true)
```

By default, when parsing the annotations, priority is as above, i.e. any value found on the command line overwrites a value found in properties, which in turn overwrites the environment variable value and so on.
This order can be customized, see [4.](#4-change-the-order-in-which-annotations-are-processed-and-use-your-own-error-messages).

#####3.2 Transform it to any object or a collection
Fields don't have to be Strings. You can configure collection fields or even any type you wish (or a collection of that type).

If you annotate the field with the @CollectionType annotation, the String is splitted and an ArrayList is created. 
The default split character is a comma, but you can also define your own:
```java
 @CollectionType(";")
```

If you want your field to be of an arbitrary type (or a collection of that type, annotate it with the
@ValueTransformer annotation and specify a class that implements the (see)FieldValueProvider interface, like so:
```java
@ValueTransformer(MyFieldValueProvider.class)
private AnyType fieldOfAnyType;
```

If you use both the @CollectionType and @ValueTransformer annotations, you will get a List of objects of the type you defined:
```java
@CollectionType
@ValueTransformer(MyFieldValueProvider.class)
private Collection<AnyType> listOfAnyType;
```

The MyFieldValueProvider.class is an inner class of your config and implements the getValue method:
```java
public class Config {
    public static class MyFieldValueProvider implements FieldValueProvider<AnyType> {
        public AnyType getValues(String optionValue) {
            <...>
        }
    }
    ...
}
```
####4. Add JSR validation annotations and/or define a custom validation method

After an instance of your config is built, it is automatically validated. You can either use JSR validation annotations
(@NotNull,...) or define a custom validation method:

```java
@Validation
private void validate() {
  <...>
}
```

####5. Change the order in which annotations are processed and use your own error messages

You can change the order in which annotations are processed globally or individually for each field.
To specify a global order for parsing ValueExtractorAnnotation annotations, annotate the class with the
@LoadingOrder annotation. To change the order for a certain field, annotate the field.
The order may only contain ValueExtractorAnnotations, i.e. 
CommandLineValue.class, PropertyValue.class and DefaultValue.class. Example:
```java
@LoadingOrder({PropertyValue.class, EnvironmentVariableValue.class, SystemPropertyValue.class, CommandLineValue.class, DefaultValue.class})
```

To specify your own error messages file (which is loaded by the PropertyLoader with the same settings as other the properties files), 
annotate the class with the @ErrorMessageFile annotation:
```java
@ErrorMessageFile("myErrorMessages")
```

####6. Build an instance of your class
```java
Config myConfig = new ConfigBuilder<Config>(Config.class).withCommandLineArgs(args).build();
```
How To Merge With An Existing Config
------------------------------------

If you already have an instance of your config class and want to only configure the fields which are not null, use
```java
newConfig = new ConfigBuilder<Config>(Config.class).withCommandLineArgs(args).merge(existingConfig);
```
<b>Note that primitive type fields are always overwritten!</b>   
Since primitive types can not be checked for 'null', it is not possible to check whether primitive fields of an existing config 
have already been set. Hence, for the moment, primitives are always overwritten.

Usage example
-------------
Say you have a config class that looks like this:
```java
@PropertiesFiles("config")    // Uses "config.properties", "config.<hostname>.properties", etc.
@PropertyLocations(directories = {"/home/user"}, contextClassLoader = true)
@PropertySuffixes(extraSuffixes = {"tngtech","myname"}, hostNames = true)
public class Config {
    public static class PidFixFactory implements FieldValueProvider<PidFix> {
        @Override
        public PidFix getValues(String optionValue) {
            <...>
        }
    }
    
    @DefaultValue("false")      // values are automatically be converted to primitive types
    @CommandLineValue(shortOpt="t", longOpt="test", hasArg=false)     // this is a flag argument
    private boolean runInTestMode;
    
    @DefaultValue("3")
    @CommandLineValue(shortOpt="rl", longOpt="runLevel", hasArg=true)
    private int runLevel;
    
    @EnvironmentVariableValue("PATH")
    @PropertyValue("path")      // maps to the key "path" in the properties file
    private String path;
 
    @SystemPropertyValue("user.name")       // maps to the field "user.name" in the system properties
    @NotEmpty("username.notEmpty")      // JSR-303 validation (Field should not be empty)
    private String userName;
 
    @CollectionType
    @ValueTransformer(PidFixFactory.class)
    @CommandLineValue(shortOpt="pc", longOpt="pidFixCollection", hasArg=true)
    private Collection<PidFix> pidFixCollection;
    
    @ValueTransformer(PidFixFactory.class)
    @CommandLineValue(shortOpt="p", longOpt="pidFix", hasArg=true)
    private PidFix pidFix;
 
    @Validation
    private void validate() {
        <...>
    }
    ...
}
```
To build a configured instance, simply call
```java
Config myConfig = new ConfigBuilder<Config>(Config.class).withCommandLineArgs(args).build();
```

Presentation
--------

A sample presentation can be found at http://tng.github.io/config-builder


Java Doc
--------

Full javadoc of the code can be found here http://tng.github.io/config-builder/javadoc
