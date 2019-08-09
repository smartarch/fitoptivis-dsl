/*
 * generated by Xtext 2.18.0.M3
 */
package cz.cuni.mff.fitoptivis.ide

import com.google.inject.Guice
import cz.cuni.mff.fitoptivis.FitLangRuntimeModule
import cz.cuni.mff.fitoptivis.FitLangStandaloneSetup
import org.eclipse.xtext.util.Modules2

/**
 * Initialization support for running Xtext languages as language servers.
 */
class FitLangIdeSetup extends FitLangStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new FitLangRuntimeModule, new FitLangIdeModule))
	}
	
}
