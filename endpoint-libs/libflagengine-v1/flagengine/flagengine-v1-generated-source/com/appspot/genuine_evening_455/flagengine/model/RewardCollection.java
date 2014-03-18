/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-02-14 18:40:25 UTC)
 * on 2014-03-18 at 04:54:44 UTC 
 * Modify at your own risk.
 */

package com.appspot.genuine_evening_455.flagengine.model;

/**
 * Model definition for RewardCollection.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the flagengine. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class RewardCollection extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Reward> rewards;

  static {
    // hack to force ProGuard to consider Reward used, since otherwise it would be stripped out
    // see http://code.google.com/p/google-api-java-client/issues/detail?id=528
    com.google.api.client.util.Data.nullOf(Reward.class);
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Reward> getRewards() {
    return rewards;
  }

  /**
   * @param rewards rewards or {@code null} for none
   */
  public RewardCollection setRewards(java.util.List<Reward> rewards) {
    this.rewards = rewards;
    return this;
  }

  @Override
  public RewardCollection set(String fieldName, Object value) {
    return (RewardCollection) super.set(fieldName, value);
  }

  @Override
  public RewardCollection clone() {
    return (RewardCollection) super.clone();
  }

}
