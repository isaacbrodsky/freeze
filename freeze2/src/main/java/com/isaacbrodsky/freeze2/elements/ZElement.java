package com.isaacbrodsky.freeze2.elements;

public enum ZElement implements Element {
    EMPTY(new Empty(), ElementDef.builder()
            .character(' ')
            .color(0x70)
            .pushable(true)
            .walkable(true)
            .name("Empty")
            .build()),
    BOARD_EDGE(new BoardEdge(), ElementDef.builder().build()),
    MESSAGE_TIMER(new MessageTimer(), ElementDef.builder().build()),
    MONITOR(new Monitor(), ElementDef.builder()
            .character(' ')
            .color(0x07)
            .cycle(1)
            .name("Monitor")
            .build()),
    PLAYER(new Player(), ElementDef.builder()
            .character(0x02)
            .color(0x1F)
            .destructible(true)
            .pushable(true)
            .visibleInDark(true)
            .cycle(1)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('Z')
            .name("Player")
            .categoryName("Items:")
            .build()),
    AMMO(new Ammo(), ElementDef.builder()
            .character(132)
            .color(0x03)
            .pushable(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('A')
            .name("Ammo")
            .build()),
    TORCH(new Torch(), ElementDef.builder()
            .character(157)
            .color(0x06)
            .visibleInDark(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('T')
            .name("Torch")
            .build()),
    GEM(new Gem(), ElementDef.builder()
            .character(4)
            .pushable(true)
            .destructible(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('G')
            .name("Gem")
            .build()),
    KEY(new Key(), ElementDef.builder()
            .character(12)
            .pushable(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('K')
            .name("Key")
            .build()),
    DOOR(new Door(), ElementDef.builder()
            .character(10)
            .color(ElementDef.COLOR_WHITE_ON_CHOICE)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('D')
            .name("Door")
            .build()),
    SCROLL(new Scroll(), ElementDef.builder()
            .character(232)
            .color(0x0F)
            .pushable(true)
            .cycle(1)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('S')
            .name("Scroll")
            .paramTextName("Edit text of scroll")
            .build()),
    PASSAGE(new Passage(), ElementDef.builder()
            .character(240)
            .color(ElementDef.COLOR_WHITE_ON_CHOICE)
            .cycle(0)
            .visibleInDark(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('P')
            .name("Passage")
            .paramBoardName("Room through passage?")
            .build()),
    DUPLICATOR(new Duplicator(), ElementDef.builder()
            .character(250)
            .color(0x0F)
            .cycle(2)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('U')
            .name("Duplicator")
            .paramDirName("Source direction?")
            .param2Name("Duplication rate?")
            .build()),
    BOMB(new Bomb(), ElementDef.builder()
            .character(11)
            .hasDrawProc(true)
            .pushable(true)
            .cycle(6)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('B')
            .name("Bomb")
            .build()),
    ENERGIZER(new Energizer(), ElementDef.builder()
            .character(127)
            .color(0x05)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('E')
            .name("Energizer")
            .build()),
    STAR(new Star(), ElementDef.builder()
            .character('S')
            .color(0x0F)
            .destructible(false)
            .cycle(1)
            .hasDrawProc(true)
            .name("Star")
            .build()),
    CONVEYOR_CW(new Conveyor(Conveyor.Direction.CW, 3), ElementDef.builder()
            .character('/')
            .cycle(3)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('1')
            .name("Clockwise")
            .categoryName("Conveyors:")
            .build()),
    CONVEYOR_CCW(new Conveyor(Conveyor.Direction.CCW, 2), ElementDef.builder()
            .character('\\')
            .cycle(2)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.ITEM)
            .editorShortcut('2')
            .name("Counter")
            .build()),
    BULLET(new Bullet(), ElementDef.builder()
            .character(248)
            .color(0x0F)
            .destructible(true)
            .cycle(1)
            .name("Bullet")
            .build()),
    WATER(new Water(), ElementDef.builder()
            .character(176)
            .color(0xF9)
            .placeableOnTop(true)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('W')
            .name("Water")
            .categoryName("Terrain:")
            .build()),
    FOREST(new Forest(), ElementDef.builder()
            .character(176)
            .color(0x20)
            .walkable(true)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('F')
            .name("Forest")
            .build()),
    SOLID(new ElementImpl(), ElementDef.builder()
            .character(219)
            .editorCategory(EditorCategory.TERRAIN)
            .categoryName("Walls:")
            .editorShortcut('S')
            .name("Solid")
            .build()),
    NORMAL(new ElementImpl(), ElementDef.builder()
            .character(178)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('N')
            .name("Normal")
            .build()),
    BREAKABLE(new ElementImpl(), ElementDef.builder()
            .character(177)
            .destructible(true)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('B')
            .name("Breakable")
            .build()),
    BOULDER(new Pushable(), ElementDef.builder()
            .character(254)
            .pushable(true)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('O')
            .name("Boulder")
            .build()),
    SLIDER_NS(new Pushable(), ElementDef.builder()
            .character(18)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('1')
            .name("Slider (NS)")
            .build()),
    SLIDER_EW(new Pushable(), ElementDef.builder()
            .character(29)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('2')
            .name("Slider (EW)")
            .build()),
    FAKE(new Fake(), ElementDef.builder()
            .character(178)
            .editorCategory(EditorCategory.TERRAIN)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('A')
            .name("Fake")
            .build()),
    INVISIBLE(new Invisible(), ElementDef.builder()
            // TODO: Invisible has different character/color in the editor
            .character(' ')
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('I')
            .name("Invisible")
            .build()),
    BLINK_WALL(new BlinkWall(), ElementDef.builder()
            .character(206)
            .cycle(1)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('L')
            .name("Blink wall")
            .param1Name("Starting time")
            .param2Name("Period")
            .paramDirName("Wall direction")
            .build()),
    TRANSPORTER(new Transporter(), ElementDef.builder()
            .character(197)
            .hasDrawProc(true)
            .cycle(2)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('T')
            .name("Transporter")
            .paramDirName("Direction?")
            .build()),
    LINE(new Line(), ElementDef.builder()
            .character(206)
            .hasDrawProc(true)
            .name("Line")
            .build()),
    RICOCHET(new ElementImpl(), ElementDef.builder()
            .character('*')
            .color(0x0A)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('R')
            .name("Ricochet")
            .build()),
    BLINK_RAY_EW(new BlinkRay(), ElementDef.builder()
            .character(205)
            .build()),
    BEAR(new Bear(), ElementDef.builder()
            .character(153)
            .color(0x06)
            .destructible(true)
            .pushable(true)
            .cycle(3)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('B')
            .name("Bear")
            .categoryName("Creatures:")
            .param1Name("Sensitivity?")
            .scoreValue(1)
            .build()),
    RUFFIAN(new Ruffian(), ElementDef.builder()
            .character(5)
            .color(0x0D)
            .destructible(true)
            .pushable(true)
            .cycle(1)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('R')
            .name("Ruffian")
            .param1Name("Intelligence?")
            .param2Name("Resting time?")
            .scoreValue(2)
            .build()),
    OBJECT(new ZObject(), ElementDef.builder()
            .character(2)
            .editorCategory(EditorCategory.CREATURE)
            .cycle(3)
            .hasDrawProc(true)
            .editorShortcut('O')
            .name("Object")
            .param1Name("Character?")
            .paramTextName("Edit Program")
            .build()),
    SLIME(new Slime(), ElementDef.builder()
            .character('*')
            .color(ElementDef.COLOR_CHOICE_ON_BLACK)
            .destructible(false)
            .cycle(3)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('V')
            .name("Slime")
            .param2Name("Movement speed?;FS")
            .build()),
    SHARK(new Shark(), ElementDef.builder()
            .character('^')
            .color(0x07)
            .destructible(false)
            .cycle(3)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('Y')
            .name("Shark")
            .param1Name("Intelligence?")
            .build()),
    SPINNING_GUN(new SpinningGun(), ElementDef.builder()
            .character(24)
            .cycle(2)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('G')
            .name("Spinning gun")
            .param1Name("Intelligence?")
            .param2Name("Firing rate?")
            .paramBulletTypeName("Firing type?")
            .build()),
    PUSHER(new Pusher(), ElementDef.builder()
            .character(16)
            .color(ElementDef.COLOR_CHOICE_ON_BLACK)
            .hasDrawProc(true)
            .cycle(4)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('P')
            .name("Pusher")
            .paramDirName("Push direction?")
            .build()),
    LION(new Lion(), ElementDef.builder()
            .character(234)
            .color(0x0C)
            .destructible(true)
            .pushable(true)
            .cycle(2)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('L')
            .name("Lion")
            .categoryName("Beasts:")
            .param1Name("Intelligence?")
            .scoreValue(1)
            .build()),
    TIGER(new Tiger(), ElementDef.builder()
            .character(227)
            .color(0x0B)
            .destructible(true)
            .pushable(true)
            .cycle(2)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('T')
            .name("Tiger")
            .param1Name("Intelligence?")
            .param2Name("Firing rate?")
            .paramBulletTypeName("Firing type?")
            .scoreValue(2)
            .build()),
    BLINK_RAY_NS(new BlinkRay(), ElementDef.builder()
            .character(186)
            .build()),
    CENTIPEDE_HEAD(new CentipedeHead(), ElementDef.builder()
            .character(233)
            .destructible(true)
            .cycle(2)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('H')
            .name("Head")
            .categoryName("Centipedes")
            .param1Name("Intelligence?")
            .param2Name("Deviance?")
            .scoreValue(1)
            .build()),
    CENTIPEDE_SEGMENT(new CentipedeSegment(), ElementDef.builder()
            .character('O')
            .destructible(true)
            .cycle(2)
            .editorCategory(EditorCategory.CREATURE)
            .editorShortcut('S')
            .name("Segment")
            .scoreValue(3)
            .build()),
    ELEMENT46(new Unknown(), ElementDef.builder().build()),
    TEXT_BLUE(new Text(), ElementDef.builder().build()),
    TEXT_GREEN(new Text(), ElementDef.builder().build()),
    TEXT_CYAN(new Text(), ElementDef.builder().build()),
    TEXT_RED(new Text(), ElementDef.builder().build()),
    TEXT_PURPLE(new Text(), ElementDef.builder().build()),
    TEXT_YELLOW(new Text(), ElementDef.builder().build()),
    TEXT_WHITE(new Text(), ElementDef.builder().build()),

    UNDEFINED(new Unknown(), ElementDef.builder().build());

    public static final Element TEXT_MIN = TEXT_BLUE;

    public final ElementImpl impl;
    public final ElementDef def;

    ZElement(ElementImpl impl, ElementDef def) {
        this.impl = impl;
        this.def = def;
    }

    @Override
    public int code() {
        return ordinal();
    }

    @Override
    public ElementImpl impl() {
        return impl;
    }

    @Override
    public ElementDef def() {
        return def;
    }

    private static final ZElement[] LOOKUP;
    static {
        LOOKUP = new ZElement[256];
        for (int i = 0; i < LOOKUP.length; i++) {
            LOOKUP[i] = UNDEFINED;
        }
        for (ZElement e : values()) {
            LOOKUP[e.code()] = e;
        }
    }

    public static ZElement forCode(int code) {
        if (code < 0 || code >= LOOKUP.length) {
            throw new RuntimeException("Failed to look up element code: " + code);
        }

        return LOOKUP[code];
    }
}
