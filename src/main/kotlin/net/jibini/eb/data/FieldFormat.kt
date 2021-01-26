package net.jibini.eb.data

import net.jibini.eb.impl.Classpath

/**
 * Processes a field value into its proper format and/or type. Its
 * input is an improperly formatted or null value, and its output is a
 * properly formatted field value as the correct type.
 *
 * Implementing classes must be annotated with [Classpath].
 *
 * @author Zach Goethel
 */
interface FieldFormat : (Any?) -> Any?