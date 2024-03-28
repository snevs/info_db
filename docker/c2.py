#!/usr/bin/env python3
import cx_Oracle

# Connect string format: [username]/[password]@//[hostname]:[port]/[DB service name]
user='system'
passw='SysPassword1'
# print("'" + user + "/" + passw + "//localhost:1521/XEPDB1")
conn = cx_Oracle.connect(user + "/" + passw + "@//localhost:1521/XEPDB1")
cur = conn.cursor()
cur.execute("SELECT TO_CHAR(SYSDATE,'DD-MON-YYYY') FROM DUAL")
res = cur.fetchall()
print(res)
cur.execute("select * from employees")
res = cur.fetchall()
print(res)