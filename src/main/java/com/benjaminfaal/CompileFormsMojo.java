package com.benjaminfaal;

import com.intellij.ant.Javac2;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import java.io.File;

/**
 * Compiles IntelliJ IDEA GUI Designer *.form files.
 *
 * @see <a href="https://www.jetbrains.com/help/idea/components-of-the-gui-designer.html">IntelliJ IDEA GUI Designer</a>.
 */
@Mojo(name = "compile-forms", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class CompileFormsMojo extends AbstractMojo {

    @Component
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        final Javac2 task = new Javac2();
        task.setProject(new Project());
        task.setSrcdir(new Path(task.getProject(), project.getBuild().getSourceDirectory()));
        task.setDestdir(new File(project.getBuild().getOutputDirectory()));
        task.setIncludes("**/*.form");

        final Path classpath = new Path(task.getProject());
        project.getArtifacts().stream()
                .filter(artifact -> artifact.getType().equals("jar") && !artifact.getScope().equals(Artifact.SCOPE_TEST))
                .forEach(artifact -> classpath.createPathElement().setLocation(artifact.getFile()));
        task.setClasspath(classpath);

        getLog().info("Compiling IDEA GUI Designer form files from: " + task.getSrcdir() + " to: " + task.getDestdir());
        try {
            task.execute();
        } catch (BuildException e) {
            throw new MojoExecutionException("Compiling IDEA GUI Designer form files failed: ", e);
        }
    }

}