/*
 * generated by Xtext 2.18.0.M3
 */
package cz.cuni.mff.fitoptivis


/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
class FitLangStandaloneSetup extends FitLangStandaloneSetupGenerated {

	def static void doSetup() {
		new FitLangStandaloneSetup().createInjectorAndDoEMFRegistration()
	}
}
