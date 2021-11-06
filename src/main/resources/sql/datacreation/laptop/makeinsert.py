# -*- coding: utf-8 -*-
#cpu,ram,storage,graphics
import random
#sqlstuff
tablename = "product"

brandname = "brand"
descriptionname = "description"
namename = "model"
categoryname = "category"
#cpuname = "cpu"
#ramname = "ram"
#storagename = "storage"
#graphicsname = "graphics"
#pricename = "price"
ratingname = "rating"
withdrawnname = "withdrawn"
specsname = "specs"
tagsname = "tags"
outputfilename = "../../inserts.sql"

def makespecs(cpu, ram, storage, graphics, brand):
    return "cpu:{},ram:{},storage:{},graphics:{},brand:{}".format(cpu, ram, storage, graphics,brand)

tags = "Computing"
lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus pretium arcu massa, non malesuada tellus sodales id."
category = "laptop"
def makeinsert(brand, name, cpu, ram, storage, graphics, i):
    rating = str(random.randint(0,5))
    #price = str(random.random() * 1.2 - 0.4)
    return 'INSERT INTO {} ({}, {}, {}, {}, {}, {}, {}, rated) VALUES ("{}", "{}", {}, "{}", "{}", "{}", {}, 1);'.format(tablename, namename, specsname, ratingname, descriptionname, categoryname, tagsname, withdrawnname, name, makespecs(cpu,ram,storage,graphics,brand), rating, lorem, category, tags, "FALSE")


ids = 0
with file(outputfilename, 'w') as fh:
    with open("laptoptelikoteliko.csv", 'r') as fhandle:
        fh.write("USE cheapset;\n")
        for i in fhandle:
            line = i.split(';')
            specs = map(lambda a: a.strip(), line[2].split(','))
            if len(specs) < 4:
                continue
            fh.write(makeinsert(line[0], line[1], specs[0], specs[1], specs[2], specs[3], ids) + '\n')
            ids += 1


    
