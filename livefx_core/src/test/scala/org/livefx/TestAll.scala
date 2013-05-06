package org.livefx

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(classOf[Suite])
@Suite.SuiteClasses(Array(
    classOf[TestGapBuffer],
    classOf[TestObservableSeq],
    classOf[TestSimpleProperty],
    classOf[TestOldVolumeTree]))
class TestAll
