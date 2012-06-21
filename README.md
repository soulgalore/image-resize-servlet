# Imagemagick servlet example 
Super simple example for using imagemagick (http://www.imagemagick.org/) from within a servlet. The servlet will convert an already existing original 
image to one of the predefined desired sizes. 

# What you need
1. Imagemagick needs to be installed and the user of the servlet engine needs to have it on the path
2. Simple as that :)

# How to use it
Access the servlet with the parameter "img" with the value of the image want.

/SERVLET/?img=MY_ORIGINAL_IMAGE-120x94.png</li>

The servlet will check if the image already exist, if so it is forwarded to the user. Else the servlet checks if the 
original image exist (named MY_ORIGINAL_IMAGE.png). If it does and the size 120x94 exists in the list of valid sizes, 
it will be created used imagemagick, and returned to the user.