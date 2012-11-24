<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Template &middot; Bootstrap</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Le styles -->
    <style type="text/css">
      body {
        padding-top: 20px;
        padding-bottom: 40px;
      }

      /* Custom container */
      .container-narrow {
        margin: 0 auto;
        max-width: 700px;
      }
      .container-narrow > hr {
        margin: 30px 0;
      }

      /* Main marketing message and sign up button */
      .jumbotron {
        margin: 60px 0;
        text-align: center;
      }
      .jumbotron h1 {
        font-size: 72px;
        line-height: 1;
      }

      .controls {
          text-align: left
      }

      .controls input, .controls select {
          width: 16em;
      }

        #form {
            padding-top: 2em;
        }

      .marketing p + h4 {
        margin-top: 28px;
      }
    </style>

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->
  </head>

  <body>
    <div class="container-narrow">
        <!--
      <div class="masthead">
        <ul class="nav nav-pills pull-right">
          <li class="active"><a href="#">Home</a></li>
          <li><a href="#">About</a></li>
          <li><a href="#">Contact</a></li>
        </ul>
        <h3 class="muted">Jenkins Plugin Skeleton Generator</h3>
      </div>
        -->

      <div class="jumbotron">
        <h1>Plugin Skeleton Generator</h1>
        <!--
          <p class="lead">
            This tool lets you generate a skeleton of a Jenkins plugin in
            your choice of the language.
        </p>
        -->

          <form class="form-horizontal" method="post" action="generate" id="form">
              <fieldset>
                  <div class="control-group"><!-- add warning CSS class-->
                      <label class="control-label" for="artifactId">Name</label>

                      <div class="controls">
                          <input type="text" class="input-xlarge" value="awesome-plugin" name="name" autofocus>
                          <!--
                          <p class="help-block">Pick the name of your plugin</p>
                          -->
                      </div>
                  </div>

                  <!--
                  <div class="control-group">
                      <label class="control-label" for="language">Language</label>

                      <div class="controls">
                          <select id="language">
                              <option>Java</option>
                              <option>Ruby</option>
                          </select>
                      </div>
                  </div>
                  -->
                  <div style="padding-top: 1em">
                      <button type="submit" class="btn btn-large btn-primary">
                          <i class="icon-download-alt icon-white"></i> Generate
                      </button>
                  </div>
              </fieldset>
          </form>
      </div>

        <hr>

        <div class="row-fluid marketing">
          <div class="span6">
            <h4>What now?</h4>
            <p>
                Once you get your skeleton generated,
                <a href="https://wiki.jenkins-ci.org/display/JENKINS/Plugin+tutorial">
                    see the Wiki page
                </a>
                for how to set up your IDE for development.
            </p>

              <h4>Host your plugin with us</h4>
              <p>
                  <a href="https://wiki.jenkins-ci.org/display/JENKINS/Hosting+Plugins">Host your plugin</a>
                  with all the other Jenkins plugins, so that we can collaborate and help you.
              </p>
          </div>

          <div class="span6">
              <h4>Stand on the shoulders of giants</h4>
              <p>
                  <a href="https://wiki.jenkins-ci.org/display/JENKINS/GitHub+Repositories">See how other plugins</a>
                  are implemented. Most plugins are under the MIT license, allowing you to cut&amp;paste.
              </p>

              <h4>Need help?</h4>
              <p>
                  Contact <a href="http://groups.google.com/group/jenkinsci-dev/topics">the developer mailing list</a>
                  for assistance, or drop by <a href="https://wiki.jenkins-ci.org/display/JENKINS/Office+Hours">
                  the office hours</a> or <a href="http://jenkins-ci.org/content/chat">IRC</a> for more
                  interactive help.
              </p>

          </div>
        </div>

    </div> <!-- /container -->

    <% adjunct("org.kohsuke.stapler.bootstrap-responsive") %>
  </body>
</html>
