#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int main() {

   for (int i=0;i<=2;i++) {

      if (fork() == 0) {

         printf("Hier ist Kindprozess %d\n",i);

		}

	}

   return 0;
}
