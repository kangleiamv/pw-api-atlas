package com.penpencil.uxcc.network.response;

import android.os.Parcel;
import android.os.Parcelable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B7\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u000b\u0010(\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010*\u001a\u0004\u0018\u00010\u0006HÆ\u0003¢\u0006\u0002\u0010\u0012J\u000b\u0010+\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010,\u001a\u00020\tHÆ\u0003JH\u0010-\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001¢\u0006\u0002\u0010.J\u0006\u0010/\u001a\u000200J\u0014\u00101\u001a\u00020!2\b\u00102\u001a\u0004\u0018\u000103HÖ\u0083\u0004J\n\u00104\u001a\u000200HÖ\u0081\u0004J\n\u00105\u001a\u00020\u0003HÖ\u0081\u0004J\u0016\u00106\u001a\u0002072\u0006\u00108\u001a\u0002092\u0006\u0010:\u001a\u000200R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR \u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\r\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\u0013\u001a\u0004\b\u0011\u0010\u0012R\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\rR\u0016\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0017\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\rR\u0011\u0010\u0019\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\rR\u0011\u0010\u001b\u001a\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u001e\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u001f\u0010\rR \u0010 \u001a\u00020!X\u0086\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b\"\u0010#\u001a\u0004\b$\u0010%\"\u0004\b&\u0010'¨\u0006;"}, d2 = {"Lcom/penpencil/uxcc/network/response/LoginData;", "Landroid/os/Parcelable;", "_accessToken", "", "_refreshToken", "_expires_in", "", "_tokenId", "user", "Lcom/penpencil/uxcc/network/response/User;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Lcom/penpencil/uxcc/network/response/User;)V", "get_accessToken", "()Ljava/lang/String;", "get_refreshToken", "set_refreshToken", "(Ljava/lang/String;)V", "get_expires_in", "()Ljava/lang/Long;", "Ljava/lang/Long;", "get_tokenId", "getUser", "()Lcom/penpencil/uxcc/network/response/User;", "access_token", "getAccess_token", "refreshToken", "getRefreshToken", "expires_in", "getExpires_in", "()J", "tokenId", "getTokenId", "loginFromTrueCaller", "", "getLoginFromTrueCaller$annotations", "()V", "getLoginFromTrueCaller", "()Z", "setLoginFromTrueCaller", "(Z)V", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/lang/String;Lcom/penpencil/uxcc/network/response/User;)Lcom/penpencil/uxcc/network/response/LoginData;", "describeContents", "", "equals", "other", "", "hashCode", "toString", "writeToParcel", "", "dest", "Landroid/os/Parcel;", "flags", "uxcc_pwProdRelease"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* loaded from: /tmp/opencode/pwdex/classes7.dex */
public final /* data */ class LoginData implements Parcelable {

    @v741("access_token")
    @Nullable
    private final String _accessToken;

    @v741("expires_in")
    @Nullable
    private final Long _expires_in;

    @v741("refresh_token")
    @Nullable
    private String _refreshToken;

    @v741("tokenId")
    @Nullable
    private final String _tokenId;
    private boolean loginFromTrueCaller;

    @v741("user")
    @NotNull
    private final User user;

    @NotNull
    public static final Parcelable.Creator<LoginData> CREATOR = new Creator();
    public static final int $stable = 8;

    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    public static final class Creator implements Parcelable.Creator<LoginData> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final LoginData createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new LoginData(parcel.readString(), parcel.readString(), parcel.readInt() == 0 ? null : Long.valueOf(parcel.readLong()), parcel.readString(), User.CREATOR.createFromParcel(parcel));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final LoginData[] newArray(int i) {
            return new LoginData[i];
        }
    }

    public LoginData(@Nullable String str, @Nullable String str2, @Nullable Long l, @Nullable String str3, @NotNull User user) {
        Intrinsics.checkNotNullParameter(user, "user");
        this._accessToken = str;
        this._refreshToken = str2;
        this._expires_in = l;
        this._tokenId = str3;
        this.user = user;
    }

    public static /* synthetic */ LoginData copy$default(LoginData loginData, String str, String str2, Long l, String str3, User user, int i, Object obj) {
        if ((i & 1) != 0) {
            str = loginData._accessToken;
        }
        if ((i & 2) != 0) {
            str2 = loginData._refreshToken;
        }
        if ((i & 4) != 0) {
            l = loginData._expires_in;
        }
        if ((i & 8) != 0) {
            str3 = loginData._tokenId;
        }
        if ((i & 16) != 0) {
            user = loginData.user;
        }
        User user2 = user;
        Long l2 = l;
        return loginData.copy(str, str2, l2, str3, user2);
    }

    public static /* synthetic */ void getLoginFromTrueCaller$annotations() {
    }

    @Nullable
    /* renamed from: component1, reason: from getter */
    public final String get_accessToken() {
        return this._accessToken;
    }

    @Nullable
    /* renamed from: component2, reason: from getter */
    public final String get_refreshToken() {
        return this._refreshToken;
    }

    @Nullable
    /* renamed from: component3, reason: from getter */
    public final Long get_expires_in() {
        return this._expires_in;
    }

    @Nullable
    /* renamed from: component4, reason: from getter */
    public final String get_tokenId() {
        return this._tokenId;
    }

    @NotNull
    /* renamed from: component5, reason: from getter */
    public final User getUser() {
        return this.user;
    }

    @NotNull
    public final LoginData copy(@Nullable String _accessToken, @Nullable String _refreshToken, @Nullable Long _expires_in, @Nullable String _tokenId, @NotNull User user) {
        Intrinsics.checkNotNullParameter(user, "user");
        return new LoginData(_accessToken, _refreshToken, _expires_in, _tokenId, user);
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoginData)) {
            return false;
        }
        LoginData loginData = (LoginData) other;
        return Intrinsics.f(this._accessToken, loginData._accessToken) && Intrinsics.f(this._refreshToken, loginData._refreshToken) && Intrinsics.f(this._expires_in, loginData._expires_in) && Intrinsics.f(this._tokenId, loginData._tokenId) && Intrinsics.f(this.user, loginData.user);
    }

    @NotNull
    public final String getAccess_token() {
        return ny81.g(this._accessToken);
    }

    public final long getExpires_in() {
        Long l = this._expires_in;
        if (l != null) {
            return l.longValue();
        }
        return 0L;
    }

    public final boolean getLoginFromTrueCaller() {
        return this.loginFromTrueCaller;
    }

    @NotNull
    public final String getRefreshToken() {
        return ny81.g(this._refreshToken);
    }

    @NotNull
    public final String getTokenId() {
        return ny81.g(this._tokenId);
    }

    @NotNull
    public final User getUser() {
        return this.user;
    }

    @Nullable
    public final String get_accessToken() {
        return this._accessToken;
    }

    @Nullable
    public final Long get_expires_in() {
        return this._expires_in;
    }

    @Nullable
    public final String get_refreshToken() {
        return this._refreshToken;
    }

    @Nullable
    public final String get_tokenId() {
        return this._tokenId;
    }

    public int hashCode() {
        String str = this._accessToken;
        int hashCode = (str == null ? 0 : str.hashCode()) * 31;
        String str2 = this._refreshToken;
        int hashCode2 = (hashCode + (str2 == null ? 0 : str2.hashCode())) * 31;
        Long l = this._expires_in;
        int hashCode3 = (hashCode2 + (l == null ? 0 : l.hashCode())) * 31;
        String str3 = this._tokenId;
        return this.user.hashCode() + ((hashCode3 + (str3 != null ? str3.hashCode() : 0)) * 31);
    }

    public final void setLoginFromTrueCaller(boolean z) {
        this.loginFromTrueCaller = z;
    }

    public final void set_refreshToken(@Nullable String str) {
        this._refreshToken = str;
    }

    @NotNull
    public String toString() {
        String str = this._accessToken;
        String str2 = this._refreshToken;
        Long l = this._expires_in;
        String str3 = this._tokenId;
        User user = this.user;
        StringBuilder a = ls00.a("LoginData(_accessToken=", str, ", _refreshToken=", str2, ", _expires_in=");
        z3r.a(l, ", _tokenId=", str3, ", user=", a);
        a.append(user);
        a.append(")");
        return a.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(@NotNull Parcel dest, int flags) {
        Intrinsics.checkNotNullParameter(dest, "dest");
        dest.writeString(this._accessToken);
        dest.writeString(this._refreshToken);
        Long l = this._expires_in;
        if (l == null) {
            dest.writeInt(0);
        } else {
            ph30.a(dest, 1, l);
        }
        dest.writeString(this._tokenId);
        this.user.writeToParcel(dest, flags);
    }
}
