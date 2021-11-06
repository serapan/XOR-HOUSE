# -*- coding: utf-8 -*-

outputfilename = "../../kinserts.sql"

tablename = "store"
namename = "name"
addressname = "address"
areaname = "area"
tkname = "postal_code"
xname = "lng"
yname = "lat"
tagsname = "tags"
withdrawnname = "withdrawn"

tags = "Computing"
def makeinsert(name, address, area, tk, x, y):
    return 'INSERT INTO {} ({}, {}, {}, {}, {}, {}, {}, {}) VALUES ("{}", "{}", "{}", "{}", {}, {}, "{}", {});'.format(tablename, namename, addressname, areaname, tkname, xname, yname, tagsname, withdrawnname, name, address, area, tk, y, x, tags, "FALSE")


with file(outputfilename, 'w') as fh:
    with open("KATASTIMATA.csv", 'r') as fhandle:
        fh.write("USE cheapset;\n")
        for i in fhandle:
            line = i.split(';')
            line[4] = line[4].replace(",", ".")
            line[5] = line[5].replace(",", ".")
            fh.write(makeinsert(*map(lambda a: a.strip(), line)) + '\n')
