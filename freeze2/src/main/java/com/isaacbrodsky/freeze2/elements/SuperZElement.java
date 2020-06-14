package com.isaacbrodsky.freeze2.elements;

public enum SuperZElement implements Element {
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
            .character(2)
            .color(0x1F)
            .cycle(1)
            .pushable(true)
            .name("Monitor")
            .build()),
    PLAYER(new Player(), ElementDef.builder()
            .character(0x02)
            .color(0x1F)
            .destructible(true)
            .pushable(true)
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
    ELEMENT6(new Unknown(), ElementDef.builder().build()),
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
    ELEMENT15(new Unknown(), ElementDef.builder().build()),
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
    ELEMENT18(new Unknown(), ElementDef.builder().build()),
    LAVA(new Lava(), ElementDef.builder()
            .character('o')
            .color(0x4E)
            .placeableOnTop(true)
            .editorCategory(EditorCategory.TERRAIN)
            .editorShortcut('L')
            .name("Lava")
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
    ELEMENT33(new Unknown(), ElementDef.builder().build()),
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
    ELEMENT38(new Unknown(), ElementDef.builder().build()),
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
    ELEMENT43(new Unknown(), ElementDef.builder().build()),
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
    FLOOR(new ElementImpl(), ElementDef.builder()
            .character(176)
            .editorCategory(EditorCategory.TERRAIN)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('F')
            .name("Floor")
            .categoryName("Terrains:")
            .build()),
    WATER_N(new ElementImpl(), ElementDef.builder()
            .character(30)
            .color(0x19)
            .editorCategory(EditorCategory.TERRAIN2)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('8')
            .name("Water N")
            .build()),
    WATER_S(new ElementImpl(), ElementDef.builder()
            .character(31)
            .color(0x19)
            .editorCategory(EditorCategory.TERRAIN2)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('2')
            .name("Water S")
            .build()),
    WATER_W(new ElementImpl(), ElementDef.builder()
            .character(17)
            .color(0x19)
            .editorCategory(EditorCategory.TERRAIN2)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('4')
            .name("Water W")
            .build()),
    WATER_E(new ElementImpl(), ElementDef.builder()
            .character(16)
            .color(0x19)
            .editorCategory(EditorCategory.TERRAIN2)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('6')
            .name("Water E")
            .build()),

    ELEMENT52(new Unknown(), ElementDef.builder().build()),
    ELEMENT53(new Unknown(), ElementDef.builder().build()),
    ELEMENT54(new Unknown(), ElementDef.builder().build()),
    ELEMENT55(new Unknown(), ElementDef.builder().build()),
    ELEMENT56(new Unknown(), ElementDef.builder().build()),
    ELEMENT57(new Unknown(), ElementDef.builder().build()),
    ELEMENT58(new Unknown(), ElementDef.builder().build()),

    ROTON(new Roton(), ElementDef.builder()
            .character(148)
            .color(0x0D)
            .destructible(true)
            .pushable(true)
            .cycle(1)
            .editorCategory(EditorCategory.UGLIES)
            .editorShortcut('R')
            .name("Roton")
            .categoryName("Uglies:")
            .param1Name("Intelligence?")
            .param2Name("Switch Rate?")
            .scoreValue(2)
            .build()),
    DRAGON_PUP(new DragonPup(), ElementDef.builder()
            .character(237)
            .color(0x04)
            .destructible(true)
            .pushable(true)
            .cycle(2)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.UGLIES)
            .editorShortcut('D')
            .name("Dragon Pup")
            .param1Name("Intelligence?")
            .param2Name("Switch Rate?")
            .scoreValue(1)
            .build()),
    PAIRER(new Pairer(), ElementDef.builder()
            .character(229)
            .color(0x01)
            .destructible(true)
            .pushable(true)
            .cycle(2)
            .editorCategory(EditorCategory.UGLIES)
            .editorShortcut('P')
            .name("Pairer")
            .param1Name("Intelligence?")
            .scoreValue(2)
            .build()),
    SPIDER(new Spider(), ElementDef.builder()
            .character(15)
            .color(0xFF)
            .destructible(true)
            .pushable(false)
            .cycle(1)
            .editorCategory(EditorCategory.UGLIES)
            .editorShortcut('S')
            .name("Spider")
            .param1Name("Intelligence?")
            .scoreValue(3)
            .build()),
    WEB(new Web(), ElementDef.builder()
            .character(197)
            .color(ElementDef.COLOR_CHOICE_ON_BLACK)
            .editorCategory(EditorCategory.TERRAIN2)
            .placeableOnTop(true)
            .walkable(true)
            .editorShortcut('W')
            .name("Web")
            .hasDrawProc(true)
            .build()),
    STONE(new Stone(), ElementDef.builder()
            .character('Z')
            .color(0x0F)
            .pushable(false)
            .cycle(1)
            .hasDrawProc(true)
            .editorCategory(EditorCategory.TERRAIN2)
            .editorShortcut('Z')
            .name("Stone")
            .build()),

    ELEMENT65(new Unknown(), ElementDef.builder().build()),
    ELEMENT66(new Unknown(), ElementDef.builder().build()),
    ELEMENT67(new Unknown(), ElementDef.builder().build()),
    ELEMENT68(new Unknown(), ElementDef.builder().build()),

    BULLET(new Bullet(), ElementDef.builder()
            .character(248)
            .color(0x0F)
            .destructible(true)
            .cycle(1)
            .name("Bullet")
            .build()),
    BLINK_RAY_EW(new BlinkRay(), ElementDef.builder()
            .character(205)
            .build()),
    BLINK_RAY_NS(new BlinkRay(), ElementDef.builder()
            .character(186)
            .build()),
    STAR(new Star(), ElementDef.builder()
            .character('S')
            .color(0x0F)
            .destructible(false)
            .cycle(1)
            .hasDrawProc(true)
            .name("Star")
            .build()),

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

    SuperZElement(ElementImpl impl, ElementDef def) {
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

    private static final SuperZElement[] LOOKUP;
    static {
        LOOKUP = new SuperZElement[256];
        for (int i = 0; i < LOOKUP.length; i++) {
            LOOKUP[i] = UNDEFINED;
        }
        for (SuperZElement e : values()) {
            LOOKUP[e.code()] = e;
        }
    }

    public static SuperZElement forCode(int code) {
        if (code < 0 || code >= LOOKUP.length) {
            throw new RuntimeException("Failed to look up element code: " + code);
        }

        return LOOKUP[code];
    }
}
