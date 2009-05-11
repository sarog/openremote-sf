<%= getVersion()%>
<%!
    /**
     * Returns either the (svn) tagged version or a temporary version number. When the
     * files are tagged by svn then the '$Name:  $' string will be substituted by cvs for
     * a tag. In that case this version number should end up in the project.
     *
     * @return either the tagged version.
     */
    String getVersion() {
      String revision = "$Revision$";
      String headUrl = "$HeadURL$";
      String version = org.openremote.irbuilder.utils.SvnUtil.getVersionLabel(headUrl,revision);
      return version;
    }
%>