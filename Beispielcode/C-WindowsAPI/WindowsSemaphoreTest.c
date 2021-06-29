/* WindowsThreadTest.c
   Zweck: Beispielcode für das Erzeugen und Schliessen von Threads mittels Windows-API
   Quelle: https://docs.microsoft.com/en-us/windows/win32/sync/using-semaphore-objects
   Modifiziert: Prof. Martin Hübner, HAW Hamburg
   Windows-Datentypen: https://docs.microsoft.com/de-de/windows/win32/winprog/windows-data-types
   * 
   * The following example uses a semaphore object to limit the number of threads that can perform a particular task. 
   * First, it uses the CreateSemaphore function to create the semaphore and to specify initial and maximum counts, 
   * then it uses the CreateThread function to create the threads.
   * Before a thread attempts to perform the task, it uses the WaitForSingleObject function to determine 
   * whether the semaphore's current count permits it to do so. 
   * The wait function's time-out parameter is set to zero, so the function returns immediately 
   * if the semaphore is in the nonsignaled state. 
   * 
   * WaitForSingleObject decrements the semaphore's count by one.
   * When a thread completes the task, it uses the ReleaseSemaphore function to increment the semaphore's count, 
   * thus enabling another waiting thread to perform the task.
   */
#include <windows.h>
#include <stdio.h>
#include <time.h>

#define MAX_SEM_COUNT 2
#define THREADCOUNT 5

HANDLE ghSemaphore;

DWORD WINAPI ThreadProc( LPVOID );

int main( void )
{
    HANDLE aThread[THREADCOUNT];
    DWORD ThreadID;
    int i;

    // Create a semaphore with initial and max counts of MAX_SEM_COUNT

    ghSemaphore = CreateSemaphore( 
        NULL,           // default security attributes
        MAX_SEM_COUNT,  // initial count
        MAX_SEM_COUNT,  // maximum count
        NULL);          // unnamed semaphore

    if (ghSemaphore == NULL) 
    {
        printf("CreateSemaphore error: %d\n", GetLastError());
        return 1;
    }

    // Create worker threads
    for( i=0; i < THREADCOUNT; i++ )
    {
        aThread[i] = CreateThread( 
                     NULL,       // default security attributes
                     0,          // default stack size
                     (LPTHREAD_START_ROUTINE) ThreadProc, 
                     NULL,       // no thread function arguments
                     0,          // default creation flags
                     &ThreadID); // receive thread identifier

        if( aThread[i] == NULL )
        {
            printf("CreateThread error: %d\n", GetLastError());
            return 1;
        }
    }

    // Wait for all threads to terminate

    WaitForMultipleObjects(THREADCOUNT, aThread, TRUE, INFINITE);

    // Close thread and semaphore handles

    for( i=0; i < THREADCOUNT; i++ )
        CloseHandle(aThread[i]);

    CloseHandle(ghSemaphore);

    return 0;
}

DWORD WINAPI ThreadProc( LPVOID lpParam )
{

    // lpParam not used in this example
    UNREFERENCED_PARAMETER(lpParam);

    DWORD dwWaitResult; 
    BOOL bContinue=TRUE;
    
    printf("Thread %d: Hello!\n", GetCurrentThreadId());

    while(bContinue)
    {
        // Try to enter the semaphore gate.
        dwWaitResult = WaitForSingleObject( 
            ghSemaphore,   // handle to semaphore
            10000L);       // time-out interval in milliseconds

        switch (dwWaitResult) 
        { 
            // The semaphore object was signaled.
            case WAIT_OBJECT_0: 
                // Simulate thread spending time on task
                printf("Thread %d: OK, working ...\n", GetCurrentThreadId());
                Sleep(3000);
 
                bContinue=FALSE;     

                // Release the semaphore when task is finished

                if (!ReleaseSemaphore( 
                        ghSemaphore,  // handle to semaphore
                        1,            // increase count by one
                        NULL) )       // not interested in previous count
                {
                    printf("ReleaseSemaphore error: %d\n", GetLastError());
                }
                break; 

            // The semaphore was nonsignaled, so a time-out occurred.
            case WAIT_TIMEOUT: 
                printf("--------------------- Thread %d: wait timed out\n", GetCurrentThreadId());
                break; 
        }
    }
    return TRUE;
}
