package edgeville.net.message.game;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Simon Pelle on 8/23/2014.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketInfo {

	public int size();

}
