#!/bin/sh

echo "----------------------------------------------"
echo " Deleting ./output ..."
echo ""

if [ -d "./output" ]
then
	rm -r output

	echo ""
	echo " Done."
	echo "----------------------------------------------"

else 
	echo ""
	echo " Nothing to clean."
	echo "----------------------------------------------"
fi


