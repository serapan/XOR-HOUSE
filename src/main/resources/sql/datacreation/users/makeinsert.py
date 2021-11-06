# -*- coding: utf-8 -*-
import bcrypt

with open("names.txt", 'r') as fhandle:
    names = filter(lambda a: len(a) > 0, fhandle.read().split('\n'))

with open("uname.txt", 'r') as fhandle:
    usernames = filter(lambda a: len(a) > 0, fhandle.read().split('\n'))

outputfilename = "../../uinserts.sql"

tablename = "user"

firstnamename = "first_name"
lastnamename = "last_name"
usernamename = "username"
passwordname = "password"
emailname = "email"
rolename = "role_id"
isloggedinname = "logged_in"
salt = bcrypt.gensalt(rounds = 10, prefix=b"2a")

def makeinsert(firstname, lastname, i, role):
    return 'INSERT INTO {} ({}, {}, {}, {}, {}, {}) VALUES ("{}", "{}", "{}", "{}", "{}", {});'.format(tablename, firstnamename, lastnamename, usernamename, passwordname, emailname, rolename, firstname, lastname, usernames[i], bcrypt.hashpw("12345", salt), usernames[i] + "@gmail.com", role)


def whatrole(i):
    if i < 45:
        return 2
    if i < 51:
        return 3
    return 1

with file(outputfilename, 'w') as fh:
    fh.write("USE cheapset;\n")
    for i in range(len(names)):
        fh.write(makeinsert(names[i].strip().split(' ')[0], names[i].strip().split(' ')[1], i, whatrole(i)) + '\n')

