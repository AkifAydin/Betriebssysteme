/* wsh.c
 * Bietet eine Kommandozeile und startet einen Prozess
 * Beispiel:> WindowsShell "C:\Program Files\Microsoft Office\Office15\winword.exe"
 * Quelle: https://msdn.microsoft.com/en-us/library/windows/desktop/ms682512%28v=vs.85%29.aspx
 * Modifikation: M. Hübner, HAW Hamburg
 */
 
#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include <stdbool.h>

int main( int argc, TCHAR *argv[] )
{
	 // Windows-Datentypen: https://docs.microsoft.com/de-de/windows/win32/winprog/windows-data-types
    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );

    char command[100];
    bool finished = false;
    
    while (!finished){
      // Show command line
      printf("Was willst du? ");
      scanf("%100s",command);

      // Built-In-Befehle pruefen:
      if (strcmp (command,"quit") == 0 || strcmp (command,"exit") == 0) {
         // Exit
         finished = true;
       } else {
          // Start the child process. 
          printf("Creating new process for %s..\n", command);    
          if( !CreateProcess( NULL,   // module name 
              command,        // Command line
              NULL,           // Process handle not inheritable
              NULL,           // Thread handle not inheritable
              FALSE,          // Set handle inheritance to FALSE
              0,              // No creation flags
              NULL,           // Use parent's environment block
              NULL,           // Use parent's starting directory 
              &si,            // Pointer to STARTUPINFO structure
              &pi )           // Pointer to PROCESS_INFORMATION structure
          ) 
          {
              printf( "----------------- Unknown Command (%d). ---------------------\n", GetLastError() );
           }
      
          // Wait until child process exits.
          printf("Waiting for new process %d with Thread %d ..\n", 
                  pi.dwProcessId, pi.dwThreadId);
          WaitForSingleObject( pi.hProcess, INFINITE );
      
          // Close process and thread handles. 
          printf("Closing handles of new process ..\n");  
          CloseHandle( pi.hProcess );
          CloseHandle( pi.hThread );
       }
    }
    return 0;
}
