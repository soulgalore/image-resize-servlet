# Image resize servlet [![Build Status](https://secure.travis-ci.org/soulgalore/image-resize-servlet.png?branch=master)](http://travis-ci.org/soulgalore/image-resize-servlet)

Resize an image using imagemagick (http://www.imagemagick.org/) or thumbnailator (http://code.google.com/p/thumbnailator/) from within a servlet. The servlet will resize an already existing original 
image to one of the predefined desired sizes (or whatever size you want, depending on configuration).

# What you need
<ol>
<li>Imagemagick needs to be installed (http://www.imagemagick.org/script/binary-releases.php or by homebrew if you use Mac OS X) and the user of the servlet engine needs to have imagemagick on the path. You need it for all tests to work, even if you will only run thumbnailator.</li>
<li> Simple as that :)</li>
</ol>

# How to make a test run in Eclipse
<ol>
<li>Checkout the project</li>
<li>Make the project a Maven project ("Convert to Maven project")</li>
<li>Start the Tomcat using https://github.com/jsimone/webapp-runner#create-a-launch-configuration</li>
<li>Access http://localhost:8080/thumbs/test-460x360.png and if you see the image, it works.</li>
</ol>

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
original image exist (named MY_ORIGINAL_IMAGE.png) and that the requested thumbnail size is valid (you can configure valid size or say that all sizes are valid)
. If the request is valid, a new thumbnail is resized and put in your configured thumbnail base dir + a generated folder path that is calculated from the original file name, so that files are spread within the file system (but all sizes for a specific file, are within the same folder). Then the new image is returned to the user. 

# Extras
Bundled with Tomcat, set it up using and inctructions of how to start: https://github.com/jsimone/webapp-runner 

Also an expire filter is setup in the web.xml to set some cache headers (so make sure to empty your browser cache if you change images but keep the same name).

Tuckeys url rewrite filter is used to setup a friendly url.

If you want to run this in production, you need to think of a couple of things:
<ol>
<li>Change the original and thumbnail directories so they exists outside the webapp (you will probably want the images to live even if you redeploy :) )</li>
<li>Make sure you set correct cache headers and add a layer in front of your servlet runner (Nginx/Apache etc) that can cache the generated image</li>
</ol>

# TODO
* Add logging
* Not tested on Windows ...



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

