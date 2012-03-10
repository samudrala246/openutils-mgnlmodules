===========================================================

${pom.name} ${pom.version}
by ${pom.organization.name}

===========================================================

${pom.description}

More information about this project can be found at:
${pom.url}

Change log and known issues can be found at:
${pom.issueManagement.url}

Source is available from the subversion repository at:
${pom.scm.url}


=========================================
 INSTALLATION
=========================================

The bundle is provided as a zip file; the archive contains a number of .jar files
(the module itself plus the required dependencies not already available in a
standard Magnolia installation.

Copy to your all the jar files in the bundle into your Magnolia instances'
WEB-INF/lib folder, just like any other module.

Please check the documentation website for information about required Magnolia
versions.


=========================================
 USING MAVEN
=========================================

The preferred way to add ${pom.name} to your project is using maven.
You can declare the following dependency in your pom.xml:

    <dependency>
      <groupId>${pom.groupId}</groupId>
      <artifactId>${pom.artifactId}</artifactId>
      <version>${pom.version}</version>
    </dependency>



=========================================
 LICENSE
=========================================

Copyright Openmind http://www.openmindonline.it

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.



=========================================
 GENERAL LEGAL NOTICES
=========================================

Magnolia is a registered trademark of Magnolia International Ltd.

