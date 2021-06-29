/* PosixGetCurrentDir.c
   Beispiel Posix-API: Ausgabe des current working directory in Unix
   */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define BUFSIZE 256
 
void main(int argc, char **argv) { 
   char Buffer[BUFSIZE];
   char* result;

   result = getcwd(Buffer, BUFSIZE);

   if( result == 0 ){
      printf("getcwd failed!\n");
   } else {
      // Output OK
      printf("\n<<<%s>>>\n", Buffer); 
   }
}
