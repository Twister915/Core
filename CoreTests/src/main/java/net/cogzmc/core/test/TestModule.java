package net.cogzmc.core.test;

import lombok.Getter;
import net.cogzmc.core.modular.ModularPlugin;
import net.cogzmc.core.modular.ModuleMeta;
import net.cogzmc.core.test.tests.ITest;
import net.cogzmc.core.test.tests.mobs.CaryTest;
import net.cogzmc.core.test.tests.pathfinding.PathfindingTest;

@ModuleMeta(name = "Testing Module", description = "Allows you to test various parts of the Core engine for display!")
public final class TestModule extends ModularPlugin {
    @Getter private static TestModule instance;
    @Getter private TestsCommand command;
    @Getter private ITest[] tests;

    @Override
    protected void onModuleEnable() throws Exception {
        instance = this;
        command = registerCommand(new TestsCommand());

        tests = new ITest[]{new PathfindingTest(), new CaryTest()};

        for (ITest test : tests) {
            test.onEnable();
        }
    }

    @Override
    protected void onModuleDisable() throws Exception {
        for (ITest test : tests) {
            test.onDisable();
        }
    }
}
