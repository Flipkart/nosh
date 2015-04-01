/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package in.hatimi.nosh.setup;

import in.hatimi.distill.polyguice.support.Polyguice;
import in.hatimi.nosh.CommandSetup;

/**
 * @author indroneel.das
 *
 */

public class PolyguiceSetup implements CommandSetup {

    private Polyguice polyguice;

    public PolyguiceSetup(Polyguice pj) {
        polyguice = pj;
    }

    @Override
    public void setup(Object command) throws Exception {
        polyguice.getComponentContext().inject(command);
    }
}
