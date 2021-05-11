#include <stdio.h>
#include <string.h>
#include <unistd.h>

#define BUFSIZE 30 + 1 + 1 // Null termination + linebreak

/*
	Wie viele "System Calls" werden durch das Programm saveuser bei einer
	Ausführung mindestens erzeugt?

	Ablauf: x = betrifft in der Theorie nur den Terminal / Prozess Speicher
		fgets, da KeyBoard Input abgerufen wird
		x printf, da nur der Output Buffer in dem Terminal bearbeitet wird
		x strlen, Buffer liegt im Prozess Speicher
		fopen
		fprintf
		fclose

	Daher werden zwischen 2 bis 4 "System Calls" ausgeführt.
	Im Hintergrund dürften es aber deutlich mehr sein.
*/

int main ()
{
	char a_acBuffer[BUFSIZE];

	if (fgets(a_acBuffer, BUFSIZE, stdin) == 0) // https://www.tutorialspoint.com/c_standard_library/c_function_fgets.htm
	{
		printf("Fehler beim einlesen des Dateinamen!\n");
		return -1;
	}

	// Remove linebreak by replacing it with the null terminator
	int a_iStrLen = strlen(a_acBuffer);

	if (a_iStrLen <= 1)
	{
		printf("Fehler beim einlesen des Dateinamen, dieser darf nicht leer sein!\n");
		return -1;
	}
	else
	{
		a_acBuffer[a_iStrLen - 1] = 0; 
	}

	printf("Name der neuen Datei: %s\n", a_acBuffer);

	FILE* a_pFile = fopen(a_acBuffer,"w"); // https://man7.org/linux/man-pages/man3/fopen.3.html

	if (a_pFile)
	{
		if (fprintf(a_pFile, "%d", getuid()) > 0) // https://man7.org/linux/man-pages/man2/getuid.2.html & https://www.tutorialspoint.com/c_standard_library/c_function_fprintf.htm
		{
			printf("Die Datei %s wurde erfolgreich erzeugt!\n", a_acBuffer);
			fclose(a_pFile);
		}
		else
		{
			printf("Die Datei %s konnte nicht erzeugt werden, fehler beim schreiben!\n", a_acBuffer);
			fclose(a_pFile);
			return -1;
		}
	}
	else
	{
		printf("Die Datei %s konnte nicht erzeugt werden!\n", a_acBuffer);
		return -1;
	}

	return 0;
}