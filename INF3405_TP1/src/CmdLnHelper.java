
public  class CmdLnHelper {

	public static void resolveCmdLine(String iCmdLine, CmdLine oReSolvedCommand)
	{
		int indexFirstSpace = iCmdLine.indexOf(" ");
		if(indexFirstSpace > 0)
		{
			oReSolvedCommand.command = iCmdLine.substring(0, indexFirstSpace);
			oReSolvedCommand.arg = iCmdLine.substring(iCmdLine.indexOf(" ")+1);
		}
		else
		{
			oReSolvedCommand.command = iCmdLine;
			oReSolvedCommand.arg = "";
		}
	}
}
