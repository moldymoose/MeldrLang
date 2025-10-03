import java.util.ArrayList; //symbol table list
import java.util.HashMap;
import java.util.Stack; //scope stack(?)
public class SimpleTableBuilder extends LittleBaseListener 
{

	private Stack<SymbolTable> scopeStack = new Stack<>();
	private ArrayList<SymbolTable> tableList = new ArrayList<>();
	private int blockCounter = 1;
	private boolean scopeError = false;

	public void prettyPrint()
	{
            //print all symbol tables in the order they were created
	       for(SymbolTable t : tableList)
		   {
				t.printContents();
				System.out.println(); //spacing
		   }
		   //System.out.println(scopeError); debug
	}

	@Override 
	public void enterProgram(LittleParser.ProgramContext ctx) 
	{ 
		SymbolTable programTable = new SymbolTable("GLOBAL");
		scopeStack.push(programTable);
		tableList.add(programTable);
	}

	@Override
	public void exitProgram(LittleParser.ProgramContext ctx) 
	{
		scopeStack.pop(); 
	}

	@Override
	public void enterFunc_decl(LittleParser.Func_declContext ctx) 
	{
		if(!scopeError)
		{
			SymbolTable funcTable = new SymbolTable(ctx.id().getText());
			scopeStack.push(funcTable);
			tableList.add(funcTable);
		} 
	}

	@Override
	public void exitFunc_decl(LittleParser.Func_declContext ctx)
	{
		if(!scopeError)
			scopeStack.pop();
	}

	@Override
	public void enterIf_stmt(LittleParser.If_stmtContext ctx) 
	{
		if(!scopeError)
		{
			SymbolTable ifTable = new SymbolTable("BLOCK " + blockCounter++);
			scopeStack.push(ifTable);
			tableList.add(ifTable);
		} 
		//System.out.println("this is a test (IF)");
	}

	@Override
	public void exitIf_stmt(LittleParser.If_stmtContext ctx) 
	{
		if(!scopeError) 
			scopeStack.pop();
	}

	@Override
	public void enterElse_part(LittleParser.Else_partContext ctx) 
	{ 
		if(ctx.getChildCount() != 0 && !scopeError) //lambda production was not used:
		{
			SymbolTable elseTable = new SymbolTable("BLOCK " + blockCounter++);
			scopeStack.push(elseTable);
			tableList.add(elseTable);
		}
	}

	@Override
	public void exitElse_part(LittleParser.Else_partContext ctx) 
	{
		if(ctx.getChildCount() != 0 && !scopeError) 
			scopeStack.pop();
	}

	@Override
	public void enterWhile_stmt(LittleParser.While_stmtContext ctx) 
	{
		if(!scopeError)
		{
			SymbolTable whileTable = new SymbolTable("BLOCK " + blockCounter++);
			scopeStack.push(whileTable);
			tableList.add(whileTable);
		} 
		//System.out.println("this is a test (WHILE)");
	}

	@Override
	public void exitWhile_stmt(LittleParser.While_stmtContext ctx) 
	{
		if(!scopeError) 
			scopeStack.pop();
	}

	@Override 
	public void enterString_decl(LittleParser.String_declContext ctx) 
	{ 
		String name = ctx.id().getText();
		if(scopeStack.peek().isAlreadyDeclared(name))
		{
			scopeStack.peek().addEntry(name, "ERROR");
			scopeError = true;
			//System.out.println("is this working?"); debug
		} else
		{
			String type = "STRING";
			String value = ctx.str().getText();
			scopeStack.peek().addEntry(name, type, value);
		}
		//Scope stack push declarations upon entering a given scope, popping top of stack upon exiting. 
		
	}

	@Override
	public void enterVar_decl(LittleParser.Var_declContext ctx) 
	{  
		String type = ctx.var_type().getText();
		String names[] = ctx.id_list().getText().split(",");
		for(String name : names)
		{
			if(scopeStack.peek().isAlreadyDeclared(name))
			{
				scopeStack.peek().addEntry(name, "ERROR");
				scopeError = true;
				//System.out.println("is this working?"); debug
			} else
			{
				scopeStack.peek().addEntry(name, type);
			}
		}
	}

	@Override
	public void enterParam_decl(LittleParser.Param_declContext ctx) 
	{ 
		String name = ctx.id().getText();
		String type = ctx.var_type().getText();
		if(scopeStack.peek().isAlreadyDeclared(name))
		{
			scopeStack.peek().addEntry(name, "ERROR");
			scopeError = true;
			//System.out.println("is this working?"); debug
		} else
		{
			scopeStack.peek().addEntry(name, type);
		}
	}

	class SymbolTable
	{
		private String scope_name;
		private ArrayList<HashMap<String, String>> declr_list;

		public SymbolTable(String scope_name)
		{
			this.scope_name = scope_name;
			declr_list = new ArrayList<>();
		}

		public void addEntry(String name, String type, String value)
		{
			HashMap<String, String> declaration = new HashMap<>();
			declaration.put("name", name);
			declaration.put("type", type);
			declaration.put("value", value);
			declr_list.add(declaration);
		}

		public void addEntry(String name, String type)
		{
			HashMap<String, String> declaration = new HashMap<>();
			declaration.put("name", name);
			declaration.put("type", type);
			declr_list.add(declaration);
		}

		public void printContents()
		{
			if(scopeError)
			{
				for(HashMap<String, String> declr : declr_list)
				{
					if(declr.get("type").equals("ERROR"))
					{
						System.out.println("DECLARATION ERROR " + declr.get("name"));
						break;
					}
				}
			} else
			{
				System.out.println("Symbol table " + scope_name);
				for(HashMap<String, String> declr : declr_list)
				{
					if(declr.get("type").equals("STRING"))
					{
						System.out.println("name " + declr.get("name") + " type " + declr.get("type") + " value " + declr.get("value"));
					} else
					{
						System.out.println("name " + declr.get("name") + " type " + declr.get("type"));
					}
				}
			}
		}

		public boolean isAlreadyDeclared(String name)
		{
			for(HashMap<String, String> declr : declr_list)
			{
				if(declr.get("name").equals(name))
				{
					return true;
				}
			}
			return false;
		}

	}
}
