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
 * Model definition for Reward.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the flagengine. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Reward extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long createdAt;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer reward;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long targetId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String targetName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long type;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long userId;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt createdAt or {@code null} for none
   */
  public Reward setCreatedAt(java.lang.Long createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Reward setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getReward() {
    return reward;
  }

  /**
   * @param reward reward or {@code null} for none
   */
  public Reward setReward(java.lang.Integer reward) {
    this.reward = reward;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTargetId() {
    return targetId;
  }

  /**
   * @param targetId targetId or {@code null} for none
   */
  public Reward setTargetId(java.lang.Long targetId) {
    this.targetId = targetId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTargetName() {
    return targetName;
  }

  /**
   * @param targetName targetName or {@code null} for none
   */
  public Reward setTargetName(java.lang.String targetName) {
    this.targetName = targetName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getType() {
    return type;
  }

  /**
   * @param type type or {@code null} for none
   */
  public Reward setType(java.lang.Long type) {
    this.type = type;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getUserId() {
    return userId;
  }

  /**
   * @param userId userId or {@code null} for none
   */
  public Reward setUserId(java.lang.Long userId) {
    this.userId = userId;
    return this;
  }

  @Override
  public Reward set(String fieldName, Object value) {
    return (Reward) super.set(fieldName, value);
  }

  @Override
  public Reward clone() {
    return (Reward) super.clone();
  }

}
