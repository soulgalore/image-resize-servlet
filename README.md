# Imagemagick resize servlet example 
Super simple example for using imagemagick (http://www.imagemagick.org/) from within a servlet. The servlet will resize an already existing original 
image to one of the predefined desired sizes (or whatever size you want, depending on configuration).

# What you need
1. Imagemagick needs to be installed and the user of the servlet engine needs to have it on the path
2. Simple as that :)

# How to use it

<ol>
<li>Setup the servlet in your web.xml (you can configure the request parameter name, original image folder, thumbnail base folder and a list of valid sizes of images)</li>

<li>Access the servlet with the your predefined request parameter with the value of the image you want.
<code>
/SERVLET/?img=MY_ORIGINAL_IMAGE-120x94.png
</code>
</li>

<li>The images is returned with that size, resized from the file MY_ORIGINAL_IMAGE.png</li>
</ol>

# How it works
The servlet will check if the image already exist in the requested size.  If the file exists, it is forwarded to the user. Else the servlet checks if the 
original image exist (named MY_ORIGINAL_IMAGE.png) and that the requested size is valid (you can configure valid size or say that all sizes are valid)
. If the request is valid, a new imaged is resized and put in your configured thumbnail base dir + a generated folder path that is calculated from the original file name, so that files are spread within the file system (but all sizes for a specific file, are within the same folder). Then the new image is returned to the user.

# Extras
Bundled with Tomcat, set it up using and inctructions of how to start: https://github.com/jsimone/webapp-runner 

Also an expire filter is setup in the web.xml to set some cache headers (so make sure to empty your browser cache if you change images but keep the same name).

TODO:
Cleanup & add more test cases ...

# License

Copyright 2012 Peter Hedenskog

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

