/* WindowsShell.c
 * Startet einen Prozess von der Kommandozeile ("shell")
 * Beispiel:> WindowsShell "C:\Program Files\Microsoft Office\Office15\winword.exe"
 * Quelle: https://msdn.microsoft.com/en-us/library/windows/desktop/ms682512%28v=vs.85%29.aspx
 */
 
#include <windows.h>
#include <stdio.h>
#include <tchar.h>

int main( int argc, TCHAR *argv[] )
{
	 // Windows-Datentypen: http://msdn.microsoft.com/en-us/library/windows/desktop/aa383751%28v=vs.85%29.aspx
    STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );

    if( argc != 2 )
    {
        printf("Usage: %s [cmdline]\n", argv[0]);
        return 1;
    }

    // Start the child process. 
	 printf("Creating new process ..\n");    
    if( !CreateProcess( NULL,   // No module name (use command line)
        argv[1],        // Command line
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
        printf( "CreateProcess failed (%d).\n", GetLastError() );
        return -1;
    }

    // Wait until child process exits.
	 printf("Waiting for new process %d with Thread %d ..\n", 
				pi.dwProcessId, pi.dwThreadId);
    WaitForSingleObject( pi.hProcess, INFINITE );

    // Close process and thread handles. 
	 printf("Closing handles of new process ..\n");  
    CloseHandle( pi.hProcess );
    CloseHandle( pi.hThread );
    
    return 0;
}
