What is it?
===========

A text-based console that can be integrated with any Java application with minimum effort. Using this console a user can continuously interact with the application from the command line. All such user operations are implemented as individual commands using APIs exposed by Nosh.

The name 'Nosh' itself is an abbreviation of the phrase 'no shell'. It underlines the fact that Nosh is not a generic-purpose shell (like Linux bash or Windows cmd.exe). It can only execute commands that are implemented using the Nash API and bundled with the corresponding application. External executables or script files cannot be launched from Nosh. Additional commands cannot be enabled without source-level modifications and re-building of the corresponding application.

Features
--------

#### Locally Accessible

All commands supported by a given Nash integration are only available locally. Nosh does not implement any network protocol or expose any TCP/UDP endpoint for remote access.

#### Interactive Shell

The interactive shell provides a familiar text-based interface complete with a prompt and allows the user to type in a command name followed by zero or more arguments. Commands are executed in sequence, one at a time. The user can execute more than one command before the shell terminates. Note that terminating the shell also constitutes a command.

#### Embedded

While the nosh binary distribution does include a stand alone mode, it is extremely limited in terms of the number of commands available. The real benefit of Nosh is realized only when it is embedded within a larger Java application.

#### Commands for Extension

Commands serve to extend the basic capabilties of Nosh with application specific features. A command is essentially a Java class that gets invoked when the user types in a command name (with optional arguments) using the interactive shell. The implementation must adhere to a very minimal set of Nosh APIs for discovery, mapping to command name, transfer of argument and invocation of main method.

Dependencies
------------

  * Any Java environment that is compliant with version 7.0 of the platform.
  * Target operating systems include any version of Microsoft Windows, Linux and MacOSX that supports a Java 7.0 platform.
