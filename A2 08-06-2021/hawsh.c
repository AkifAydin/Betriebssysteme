#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <stdbool.h>
#include <stdlib.h>
#include <sys/wait.h>
#include <linux/limits.h>

#define SHELL_VER		 0.987
#define COMMAND_BUFSIZE  128
#define PARAM_BUFSIZE 	 128
#define MACRO_TOS_END(t) #t
#define MACRO_TOS(t)	 MACRO_TOS_END(t)

/*
	Testen Sie Ihr hawsh-Programm bzgl. aller Built-In-Befehle, der UNIX-Befehle ps, ls und env
	sowie eines nicht existierenden Befehls (z.B. abcde), und zwar jeweils mit Ausführung im
	Vordergrund und im Hintergrund (&)!

	- Built-In-Befehle: funktionieren
	- UNIX-Befehle ps, ls und env: funktionieren
	- nicht existierenden Befehls (z.B. abcde): funktionieren (Geben aber keine Ausgabe)
	- leere Befehle: funktionieren
*/

void type_prompt()
{
	char a_acCWD[PATH_MAX];
   	getcwd(a_acCWD, sizeof(a_acCWD));
	printf("%s - Was willst du, %s? ", a_acCWD, getenv("USER"));
}

void read_command(char i_acCommand[], char i_acParams[])
{
	// Buffer reset
	i_acCommand[0] = i_acParams[0] = 0;
	
	char a_acBuffer[COMMAND_BUFSIZE + PARAM_BUFSIZE + 2]; // Nulltermination + linebreak

	if (fgets(a_acBuffer, sizeof(a_acBuffer), stdin) == 0) // https://www.tutorialspoint.com/c_standard_library/c_function_fgets.htm
	{
		printf("Fehler beim einlesen des Befehls!\n");
		return;
	}

	// Remove linebreak by replacing it with the null terminator
	int a_iStrLen = strlen(a_acBuffer);

	if (a_iStrLen == 1)
	{
		printf("Fehler beim einlesen des Befehls, dieser darf nicht leer sein!\n");
		return;
	}
	else
	{
		a_acBuffer[a_iStrLen-1] = 0;
	}

	// Parse out command and params
	sscanf(a_acBuffer, "%" MACRO_TOS(COMMAND_BUFSIZE) "s %" MACRO_TOS(PARAM_BUFSIZE) "[^\n]", i_acCommand, i_acParams);	
}

bool process_internalCommand(char i_acCommand[], char i_acParams[])
{
	if (!strcmp(i_acCommand, "quit"))
	{
		printf("... und tschüß!\n");
		exit(0);
	}
	else if (!strcmp(i_acCommand, "version"))
	{
		printf("HAW-Shell Version %f Autor: Finn-Lukas Armbruster & Daniel Bergmann\n", SHELL_VER);
	}
	else if (!strcmp(i_acCommand, "help"))
	{
		printf("Verwendung der HAW Shell: Command [Optionen]\n");
		printf("Es können dabei entweder normale Befehle ausgeführt werden oder eine der folgenden 'Built-In' Befehle:\n");
		printf("Name des Befehls\tWirkung des Befehls\n");
		printf("quit\t\t\tBeenden der HAW-Shell\n");
		printf("version\t\t\tAnzeige des Autors und der Versionsnummer der HAW-Shell\n");
		printf("help\t\t\tAnzeige der möglichen Built-In-Befehle mit Kurzbeschreibung\n");
	}	 
	else if (!!strcmp(i_acCommand, ""))
	{
		return false;
	}

	return true;
}

int main ()
{
	char a_acCommand[COMMAND_BUFSIZE];
	char a_acParams[PARAM_BUFSIZE];

	while (true)
	{
		type_prompt();
		read_command(&a_acCommand[0], &a_acParams[0]);
		if (!process_internalCommand(&a_acCommand[0], &a_acParams[0]))
		{
			// Check for '&' at the end of the command!
			size_t a_iCmdLen = strlen(a_acCommand);
			bool a_fWaitForChild = a_acCommand[a_iCmdLen - 1] != '&';

			if (!a_fWaitForChild)
			{
				a_acCommand[a_iCmdLen - 1] = 0;
			}
			
			int a_iPID = fork();

			if (a_iPID < 0)
			{
				printf("Unable to fork!");
			}
			else if (a_iPID > 0) // Parent branch
			{
				if (a_fWaitForChild)
				{
					// Waiting for child
					waitpid(a_iPID, 0, 0);
				}	
			}
			else // a_iPID == 0, Child-Program branch
			{
				strlen(a_acParams) == 0 ? execlp(a_acCommand, a_acCommand, 0) : execlp(a_acCommand, a_acCommand, a_acParams, 0);
				return true;
			}
		}
	}

	return 0;
}