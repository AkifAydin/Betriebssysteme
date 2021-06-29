#include <windows.h> 
#include <stdio.h>
#include <tchar.h>

#define BUFSIZE 5 // MAX_PATH
 
void _tmain(int argc, TCHAR **argv) 
{ 
   TCHAR Buffer[BUFSIZE];
   DWORD dwRet;

   dwRet = GetCurrentDirectory(BUFSIZE, Buffer);

   if( dwRet == 0 )
   {
      printf("GetCurrentDirectory failed (%d)\n", GetLastError());
   } else if(dwRet > BUFSIZE)
   {
      printf("Buffer too small; need %d characters\n", dwRet);
   } else {
      // Output OK
      _tprintf(TEXT("\n<<<%s>>>\n"), Buffer); 
   }
}
