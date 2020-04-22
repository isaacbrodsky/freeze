/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.superz.DragonPup;
import com.isaacbrodsky.freeze.elements.superz.Floor;
import com.isaacbrodsky.freeze.elements.superz.Lava;
import com.isaacbrodsky.freeze.elements.superz.Pairer;
import com.isaacbrodsky.freeze.elements.superz.Roton;
import com.isaacbrodsky.freeze.elements.superz.Spider;
import com.isaacbrodsky.freeze.elements.superz.Stone;
import com.isaacbrodsky.freeze.elements.superz.SuperAmmo;
import com.isaacbrodsky.freeze.elements.superz.SuperBear;
import com.isaacbrodsky.freeze.elements.superz.SuperForest;
import com.isaacbrodsky.freeze.elements.superz.SuperPlayer;
import com.isaacbrodsky.freeze.elements.superz.SuperWater;
import com.isaacbrodsky.freeze.elements.superz.Web;
import com.isaacbrodsky.freeze.utils.TimeAndMathUtils;

/**
 * @author isaac
 * 
 */
public final class ElementResolver {
	public ElementResolver(EmuMode emu) {
		_aIR(0x00, Empty.class);// EMPTY #00
		_aIR(0x01, BoardEdge.class); // BOARD EDGE #01
		_aIR(0x02, GlitchElement.Messenger.class); // MESSENGER #02
		_aIR(0x03, Monitor.class); // MONITOR #03

		_aIR(0x07, Gem.class); // GEM #07
		_aIR(0x08, Key.class); // KEY #08
		_aIR(0x09, Door.class); // DOOR #09
		_aIR(0x0A, Scroll.class); // SCROLL #10
		_aIR(0x0B, Passage.class); // PASSAGE #11
		_aIR(0x0C, Duplicator.class); // BOMB #13
		_aIR(0x0D, Bomb.class); // BOMB #13
		_aIR(0x0E, Energizer.class); // ENERGIZER #14

		_aIR(0x10, Conveyor.ConveyorCW.class); // CLOCKWISE #16
		_aIR(0x11, Conveyor.ConveyorCCW.class); // COUNTERCLOCKWISE #17

		_aIR(0x15, Solid.class); // SOLID #21
		_aIR(0x16, Normal.class); // NORMAL #22
		_aIR(0x17, Breakable.class); // BREAKABLE #23
		_aIR(0x18, Boulder.class); // BOULDER #24
		_aIR(0x19, Slider.SliderNS.class); // SLIDERNS #25
		_aIR(0x1A, Slider.SliderEW.class); // SLIDEREW #26
		_aIR(0x1B, Fake.class); // FAKE #27
		_aIR(0x1C, Invisible.class); // INVISIBLE #28
		_aIR(0x1D, BlinkWall.class); // BLINK WALL #29
		_aIR(0x1E, Transporter.class); // TRANSPORTER #30
		_aIR(0x1F, Line.class); // LINE #31
		_aIR(0x20, Ricochet.class); // RICOCHET #32
		_aIR(0x23, Ruffian.class); // RUFFIAN #35
		_aIR(0x24, ZObject.class); // OBJECT #36
		_aIR(0x25, Slime.class); // SLIME #37
		_aIR(0x26, Shark.class); // SHARK #38
		_aIR(0x27, SpinningGun.class); // SPINNINGGUN #39
		_aIR(0x28, Pusher.class); // PUSHER #40
		_aIR(0x29, Lion.class); // LION #41
		_aIR(0x2A, Tiger.class); // TIGER #42
		_aIR(0x2C, Centipede.Head.class); // HEAD #44
		_aIR(0x2D, Centipede.Segment.class); // SEGMENT #45
		_aIR(0x2E, GlitchElement.Element46.class); // Unknown #46

		_aSR("empty", 0x00);
		_aSR("boardedge", 0x01);
		_aSR("player", 0x04);
		_aSR("ammo", 0x05);
		_aSR("gem", 0x07);
		_aSR("key", 0x08);
		_aSR("door", 0x09);
		_aSR("scroll", 0x0A);
		_aSR("passage", 0x0B);
		_aSR("duplicator", 0x0C);
		_aSR("bomb", 0x0D);
		_aSR("energizer", 0x0E);
		_aSR("clockwise", 0x10);
		_aSR("counter", 0x11);
		_aSR("forest", 0x14);
		_aSR("solid", 0x15);
		_aSR("normal", 0x16);
		_aSR("breakable", 0x17);
		_aSR("boulder", 0x18);
		_aSR("sliderns", 0x19);
		_aSR("sliderew", 0x1A);
		_aSR("fake", 0x1B);
		_aSR("invisible", 0x1C);
		_aSR("blinkwall", 0x1D);
		_aSR("transporter", 0x1E);
		_aSR("line", 0x1F);
		_aSR("ricochet", 0x20);
		_aSR("bear", 0x22);
		_aSR("ruffian", 0x23);
		_aSR("object", 0x24);
		_aSR("slime", 0x25);
		_aSR("shark", 0x26);
		_aSR("spinninggun", 0x27);
		_aSR("pusher", 0x28);
		_aSR("lion", 0x29);
		_aSR("tiger", 0x2A);
		_aSR("head", 0x2C);
		_aSR("segment", 0x2D);

		if (emu.equals(EmuMode.ZZT)) {
			_aIR(0x04, Player.class); // PLAYER #04
			_aIR(0x05, Ammo.class); // AMMO #05
			_aIR(0x06, Torch.class); // TORCH #06

			_aIR(0x0F, Star.class); // STAR #15
			_aIR(0x12, Bullet.class); // BULLET #18
			_aIR(0x13, Water.class); // WATER #19
			_aIR(0x14, Forest.class); // FOREST #20
			_aIR(0x22, Bear.class); // BEAR #34

			textBase = 0x2F;
			_aIR(0x2F, Text.class); // BLUE #47
			_aIR(0x30, Text.class); // GREEN #48
			_aIR(0x31, Text.class); // CYAN #49
			_aIR(0x32, Text.class); // RED #50
			_aIR(0x33, Text.class); // PURPLE #51
			_aIR(0x34, Text.class); // BROWN #52
			_aIR(0x35, Text.class); // WHITE #53

			_aIR(0x21, BlinkWallRay.HorizontalRay.class); // BLINK WALL RAY
			// HORIZ
			// #33
			_aIR(0x2B, BlinkWallRay.VerticalRay.class); // BLINK WALL RAY VERTI
			// #43

			_aSR("bullet", 0x12);
			_aSR("star", 0x0F);
			_aSR("torch", 0x06);
			_aSR("water", 0x13);
		} else if (emu.equals(EmuMode.SUPERZZT)) {
			_aIR(0x04, SuperPlayer.class); // PLAYER #04
			_aIR(0x05, SuperAmmo.class); // AMMO #05

			_aIR(0x13, Lava.class); // LAVA #19
			_aIR(0x14, SuperForest.class); // FOREST #20

			_aIR(0x22, SuperBear.class); // BEAR #34
			_aIR(0x2F, Floor.class); // FLOOR

			_aIR(0x30, SuperWater.WaterN.class); // WATER N
			_aIR(0x31, SuperWater.WaterS.class); // WATER S
			_aIR(0x32, SuperWater.WaterW.class); // WATER W
			_aIR(0x33, SuperWater.WaterE.class); // WATER E

			_aIR(0x3B, Roton.class); // ROTON #59
			_aIR(0x3C, DragonPup.class); // DRAGON PUP #60
			_aIR(0x3D, Pairer.class); // PAIRER #61
			_aIR(0x3E, Spider.class); // SPIDER #62
			_aIR(0x3F, Web.class); // WEB #63
			_aIR(0x40, Stone.class); // STONE OF POWER #64

			_aIR(0x45, Bullet.class); // BULLET #69
			_aIR(0x46, BlinkWallRay.HorizontalRay.class); // BLINK WALL RAY
			// HORIZ
			// #33
			_aIR(0x47, BlinkWallRay.VerticalRay.class); // BLINK WALL RAY VERTI
			// #43
			_aIR(0x48, Star.class);

			textBase = 0x49;
			_aIR(0x49, Text.class); // BLUE #73
			_aIR(0x4A, Text.class); // GREEN #74
			_aIR(0x4B, Text.class); // CYAN #75
			_aIR(0x4C, Text.class); // RED #76
			_aIR(0x4D, Text.class); // PURPLE #77
			_aIR(0x4E, Text.class); // BROWN #78
			_aIR(0x4F, Text.class); // WHITE #79

			_aSR("floor", 0x2F);
			_aSR("watern", 0x30);
			_aSR("waters", 0x31);
			_aSR("waterw", 0x32);
			_aSR("watere", 0x33);
			_aSR("roton", 0x3B);
			_aSR("dragonpup", 0x3C);
			_aSR("spider", 0x3E);
			_aSR("web", 0x3F);
			_aSR("stone", 0x40);
			_aSR("bullet", 0x45);
			_aSR("star", 0x48);
			_aSR("lava", 0x13);
		} else {
			textBase = -1;
		}
	}

	public Element createInstance(Class<Element> clazz) {
		try {
			Constructor<Element> defBlankConstructor = clazz.getConstructor();
			return defBlankConstructor.newInstance();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * Add integer resolver
	 * 
	 * @param k
	 * @param value
	 */
	private void _aIR(int k, Class value) {
		_iresolver.put(k, value.asSubclass(Element.class));
	}

	/**
	 * Add String resolver. Forces the String to lower case. All operations on
	 * <code>_sresolver</code> should use lower case Strings.
	 * 
	 * @param k
	 * @param value
	 */
	private void _aSR(String k, int value) {
		// force everything to lowercase.
		_sresolver.put(k.toLowerCase(), value);
	}

	private final Map<Integer, Class<Element>> _iresolver = new HashMap<Integer, Class<Element>>();
	private final Map<String, Integer> _sresolver = new HashMap<String, Integer>();

	private final int textBase;

	/**
	 * @param type
	 * @param color
	 * @param x
	 * @param y
	 * @return
	 */
	public Element resolve(int type, int color, int x, int y) {
		if (type < 0)
			throw new IllegalArgumentException("type code < 0");
		
		Element e = null;

		if (_iresolver.containsKey(type)) {
			Class<Element> clazz = _iresolver.get(type);
			e = createInstance(clazz);
			if (e != null) {
				e.createInstance(new SaveData(type, color));
				e.setXY(x, y);
			}
		}

		if (e == null) {
			e = createInstance((Class) Unimplemented.class);
			e.createInstance(new SaveData(type, color));
			e.setXY(x, y);
		}

		return e;
	}

	/**
	 * Resolves <code>name</code> to an element type and creates an Element (if
	 * possible, as with {{@link #resolve(int, int, int, int)}) and returns it.
	 * Returns <code>null</code> on failure.
	 * 
	 * @param name
	 * @param color
	 * @param x
	 * @param y
	 * @return
	 */
	public Element resolve(String name, int color, int x, int y) {
		if (_sresolver.containsKey(name)) {
			int type = _sresolver.get(name);
			return resolve(type, color, x, y);
		} else {
			// try to use FZ class name
			System.err.println("Unknown name!!! " + name);
			for (int i : _iresolver.keySet()) {
				Class<Element> e = _iresolver.get(i);
				if (e.getSimpleName().equalsIgnoreCase(name)) {
					return resolve(i, color, x, y);
				}
			}

			return null;
		}
	}

	/**
	 * @param clazz
	 * @param color
	 * @param x
	 * @param y
	 * @return
	 */
	public Element resolve(Class clazz, int color, int x, int y) {
		Element e = createInstance(clazz);
		if (e != null) {
			e.createInstance(new SaveData(reverseClass(clazz), color));
			e.setXY(x, y);
		}
		return e;
	}

	/**
	 * Returns a type code from the given name, or <code>-1</code> if the name
	 * is not legal. Names are case insensitive.
	 * 
	 * @param name
	 * @return
	 */
	public int codeFromName(String name) {
		name = name.toLowerCase();
		if (_sresolver.containsKey(name))
			return _sresolver.get(name);
		else
			return -1;
	}

	/**
	 * @param class1
	 * @return
	 */
	public int codeFromClass(Class<? extends Element> class1) {
		for (int i : _iresolver.keySet()) {
			if (_iresolver.get(i).equals(class1))
				return i;
		}
		return -1;
	}

	/**
	 * Returns the class implementing the element type of the given name, or
	 * <code>null</code> if the name is not legal. Names are case insensitive.
	 * 
	 * @param name
	 * @return
	 */
	public Class<Element> classFromName(String name) {
		name = name.toLowerCase();
		if (_sresolver.containsKey(name)) {
			int type = _sresolver.get(name);
			return _iresolver.get(type);
		} else {
			return null;
		}
	}

	/**
	 * @param clazz
	 * @return
	 */
	public int reverseClass(Class clazz) {
		for (int i : _iresolver.keySet()) {
			if (_iresolver.get(i).equals(clazz))
				return i;
		}

		return -1;
	}

	/**
	 * @param saveData
	 * @param i
	 * @param j
	 * @return
	 */
	public Element resolve(SaveData sav, int x, int y) {
		return resolve(sav.getType(), sav.getColor(), x, y);
	}

	/**
	 * @return
	 */
	public int getTextBase() {
		return textBase;
	}

	/**
	 * Cached editor information
	 */
	private int maxlen = -1, maxid = -1;
	private char[] editorcodes;
	private int[] editorcodesresolve;

	private void calculateCachedInformation() {
		for (int i = 0; i < 256; i++) {
			Class<Element> c = _iresolver.get(i);
			if (c == null)
				continue;

			String s = c.getSimpleName();
			if (s.length() > maxlen)
				maxlen = s.length();
			if (i > maxid)
				maxid = i;
		}

		editorcodes = new char[10 + 26 + 26 + 10];
		for (int i = 0; i < 10; i++)
			editorcodes[i] = (char) ('0' + i);
		for (int i = 0; i < 26; i++)
			editorcodes[i + 10] = (char) ('a' + i);
		for (int i = 0; i < 26; i++)
			editorcodes[i + 10 + 26] = (char) ('A' + i);
		editorcodes[10 + 26 + 26 + 0] = '!';
		editorcodes[10 + 26 + 26 + 1] = '@';
		editorcodes[10 + 26 + 26 + 2] = '#';
		editorcodes[10 + 26 + 26 + 3] = '$';
		editorcodes[10 + 26 + 26 + 4] = '%';
		editorcodes[10 + 26 + 26 + 5] = '^';
		editorcodes[10 + 26 + 26 + 6] = '&';
		editorcodes[10 + 26 + 26 + 7] = '*';
		editorcodes[10 + 26 + 26 + 8] = '(';
		editorcodes[10 + 26 + 26 + 9] = ')';

		editorcodesresolve = new int[256];
		Arrays.fill(editorcodesresolve, -1);
		int cidx = 0;
		for (int i = 0; i < 256; i++) {
			Class<Element> c = _iresolver.get(i);
			if (c == null)
				continue;

			editorcodesresolve[editorcodes[cidx]] = i;

			cidx++;
		}
	}

	/**
	 * @return
	 */
	public String makeElementList(boolean useCodes) {
		if (maxlen == -1 || maxid == -1)
			calculateCachedInformation();

		int iddeclen;
		if (useCodes)
			iddeclen = 1;
		else
			iddeclen = Integer.toString(maxid, 10).length();

		ArrayList<StringBuilder> list = new ArrayList<StringBuilder>(22);

		int y = 0, idx = 0;
		for (int i = 0; i < 256; i++) {
			Class<Element> c = _iresolver.get(i);
			if (c == null)
				continue;

			StringBuilder cur;
			if (list.size() <= y) {
				cur = new StringBuilder();
				list.add(y, cur);
			} else {
				cur = list.get(y);
			}

			StringBuilder tmpName = new StringBuilder(c.getSimpleName());
			while (tmpName.length() < maxlen)
				tmpName.append(' ');

			String code;
			if (useCodes)
				code = '\001' + Character.toString(editorcodes[idx]) + '\002';
			else
				code = TimeAndMathUtils.padInt(i, iddeclen, '0');

			cur.append(code).append(" ").append(tmpName.toString()).append(" ");

			y++;
			if (y == 22) {
				y = 0;
			}
			idx++;
		}

		StringBuilder sb = new StringBuilder();

		// int made = 0, y = 0, x = 0;
		// for (int i = 0; i < 255; i++) {
		// Class<Element> c = _iresolver.get(i);
		// if (c == null)
		// continue;
		//			
		// StringBuilder tmpName = new StringBuilder(c.getSimpleName());
		// while (tmpName.length() < maxlen)
		// tmpName.append(' ');
		//			
		// sb.append(TimeAndMathUtils.padInt(i, 3, '0') + ' ' + tmpName);
		//			
		// y++;
		// if (y == 3) {
		// sb.append('\n');
		// y = 0;
		// x++;
		// }
		// }

		for (int i = 0; i < list.size(); i++) {
			StringBuilder cur = list.get(i);
			if (cur == null)
				continue;
			sb.append(cur).append('\n');
		}

		return sb.toString();
	}

	/**
	 * @param cmd
	 * @return
	 */
	public int resolveEditorCode(String cmd) {
		// char c = cmd.charAt(0);
		// for (int i = 0; i < editorcodes.length; i++) {
		// if (editorcodes[i] == c)
		// return i;
		// }
		// return -1;
		return editorcodesresolve[cmd.charAt(0)];
	}
}
