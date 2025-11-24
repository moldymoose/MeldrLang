# Using Meldr on the Osprey Web Server (Bonus Section for COP4620)

## What you'll need
- Implementation Package Includes:
	 - Micro.sh (UPDATED)
	 - MeldrLang.g4 
	 - Java Source Files 
		 - Driver.java
		 - BlenderObject.java
		 - PropertyTypes.java
		 - PythonBuilder.java
	 - TestInputs folder (.meldr files)
	 - TestOutputs folder (.out files)
 - ANTLR jar (tested using version 4.9.3)

## Setting it up

This process will assume ANTLR 4.9.3 is already installed on your partition of the web server (if not, see [installation process](https://github.com/antlr/antlr4/blob/master/doc/getting-started.md) for ANTLR on GitHub).

 1. Unzip implementation zip file in working directory. (should contain a folder called `my_dir`
 2. Change the CLASSPATH environmental variable in your bash profile to point to `my_dir`. 
> Example: `/home/18/n01551618/compiler/my_dir:$CLASSPATH`.



## Running inputs (*.meldr)
To run inputs, process should be identical to previous steps (1-3) in which the following commands should be entered into the terminal (working directory should be `my_dir`:

 1. `chmod +x ./Micro.sh`   to grant executable permissions. 
 2. `./Micro.sh <put input .meldr file here>` to run Meldr.
 3. *Two* possible outcomes: folder will be generated (`./my_dir/output`) containing a .py file **or** terminal will show semantic and syntax errors associated with the .meldr file. 

 4. Use the `diff` command as usual to test either outcome against the associated .out files provided in the implementation package.
