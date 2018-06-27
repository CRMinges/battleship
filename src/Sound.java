import java.io.*;
import java.applet.*;
import java.net.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 * @author mm4541,cm9246,wb3978
 * The class sound is extracing the sound from the sound file
 * that are going to be used int the game when user hits,misses
 * win or lose. Also it have a method that will constatly loop
 * the sonar sound as the beckground sound during the game play
 *
 */
public class Sound {

	static AudioClip splash, victorious,loser,thunder;/* sound names that are goign to be used */
	{
		try {	
			thunder = Applet.newAudioClip(new URL("/resources/sounds/thunder.wav"));
			splash = Applet.newAudioClip(new URL("/resources/sounds/splashNew.wav"));
			victorious = Applet.newAudioClip(new URL("/resources/sounds/victorious.wav"));
			loser = Applet.newAudioClip(getClass().getResource("/sounds/loser.wav"));
			
		}catch(MalformedURLException mue){}	
	}
	
	/* loops the sonar sound */
	public void sonarSound() throws LineUnavailableException, UnsupportedAudioFileException, IOException{
		AudioClip clip = Applet.newAudioClip(getClass().getResource("/sounds/sonar_sub.wab"));
		clip.loop();
	}
}
