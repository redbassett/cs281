#! /usr/bin/env python

import sys, string

if len(sys.argv) == 2:
	print 'Using file ',sys.argv[1]
	file = open('src/'+sys.argv[1]);
	charList = {}
	while 1:
		lines = file.readlines(1000);
		if not lines:
			break
		for line in lines:
			for character in line:
				if character in charList:
					charList[character] += 1
				else:
					charList[character] = 1
	print charList
else:
	print 'Please include a single file to proccess.';
