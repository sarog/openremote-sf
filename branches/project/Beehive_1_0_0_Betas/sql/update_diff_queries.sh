mysql -u root -p --database=beehive -e\
"select model.name, vendor.name \
 from vendor, model \
 where vendor.oid = model.vendor_oid \
 order by vendor.name" > model.output1.txt


mysql -u root -p --database=beehive_1_0_0 -e\
"select model.name, vendor.name \
 from vendor, model \
 where vendor.oid = model.vendor_oid \
 order by vendor.name" > model.output2.txt

diff -y --suppress-common-lines model.output1.txt  model.output2.txt | less
