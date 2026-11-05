package com.penpencil.uxcc.network.response;

import android.os.Parcel;
import android.os.Parcelable;
import com.penpencil.uxcc.utils.Constants;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0016\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\tHÆ\u0003¢\u0006\u0002\u0010\u0013J8\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tHÆ\u0001¢\u0006\u0002\u0010\u001cJ\u0006\u0010\u001d\u001a\u00020\tJ\u0014\u0010\u001e\u001a\u00020\u00032\b\u0010\u001f\u001a\u0004\u0018\u00010 HÖ\u0083\u0004J\n\u0010!\u001a\u00020\tHÖ\u0081\u0004J\n\u0010\"\u001a\u00020#HÖ\u0081\u0004J\u0016\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020\tR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\"\u0010\b\u001a\u0004\u0018\u00010\t8\u0006@\u0006X\u0087\u000e¢\u0006\u0010\n\u0002\u0010\u0016\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015¨\u0006)"}, d2 = {"Lcom/penpencil/uxcc/network/response/LoginResponse;", "Landroid/os/Parcelable;", Constants.SUCCESS_CAP, "", "data", "Lcom/penpencil/uxcc/network/response/LoginData;", "error", "Lcom/penpencil/core/network/result/Error;", "status", "", "<init>", "(ZLcom/penpencil/uxcc/network/response/LoginData;Lcom/penpencil/core/network/result/Error;Ljava/lang/Integer;)V", "getSuccess", "()Z", "getData", "()Lcom/penpencil/uxcc/network/response/LoginData;", "getError", "()Lcom/penpencil/core/network/result/Error;", "getStatus", "()Ljava/lang/Integer;", "setStatus", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "component1", "component2", "component3", "component4", "copy", "(ZLcom/penpencil/uxcc/network/response/LoginData;Lcom/penpencil/core/network/result/Error;Ljava/lang/Integer;)Lcom/penpencil/uxcc/network/response/LoginResponse;", "describeContents", "equals", "other", "", "hashCode", "toString", "", "writeToParcel", "", "dest", "Landroid/os/Parcel;", "flags", "uxcc_pwProdRelease"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* loaded from: /tmp/opencode/pwdex/classes7.dex */
public final /* data */ class LoginResponse implements Parcelable {

    @v741("data")
    @NotNull
    private final LoginData data;

    @v741("error")
    @NotNull
    private final com.penpencil.core.network.result.Error error;

    @v741("status")
    @Nullable
    private Integer status;

    @v741(Constants.SUCCESS_CAP)
    private final boolean success;

    @NotNull
    public static final Parcelable.Creator<LoginResponse> CREATOR = new Creator();
    public static final int $stable = 8;

    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    public static final class Creator implements Parcelable.Creator<LoginResponse> {
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final LoginResponse createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new LoginResponse(parcel.readInt() != 0, LoginData.CREATOR.createFromParcel(parcel), parcel.readParcelable(LoginResponse.class.getClassLoader()), parcel.readInt() == 0 ? null : Integer.valueOf(parcel.readInt()));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public final LoginResponse[] newArray(int i) {
            return new LoginResponse[i];
        }
    }

    public LoginResponse(boolean z, @NotNull LoginData loginData, @NotNull com.penpencil.core.network.result.Error error, @Nullable Integer num) {
        Intrinsics.checkNotNullParameter(loginData, "data");
        Intrinsics.checkNotNullParameter(error, "error");
        this.success = z;
        this.data = loginData;
        this.error = error;
        this.status = num;
    }

    public static /* synthetic */ LoginResponse copy$default(LoginResponse loginResponse, boolean z, LoginData loginData, com.penpencil.core.network.result.Error error, Integer num, int i, Object obj) {
        if ((i & 1) != 0) {
            z = loginResponse.success;
        }
        if ((i & 2) != 0) {
            loginData = loginResponse.data;
        }
        if ((i & 4) != 0) {
            error = loginResponse.error;
        }
        if ((i & 8) != 0) {
            num = loginResponse.status;
        }
        return loginResponse.copy(z, loginData, error, num);
    }

    /* renamed from: component1, reason: from getter */
    public final boolean getSuccess() {
        return this.success;
    }

    @NotNull
    /* renamed from: component2, reason: from getter */
    public final LoginData getData() {
        return this.data;
    }

    @NotNull
    /* renamed from: component3, reason: from getter */
    public final com.penpencil.core.network.result.Error getError() {
        return this.error;
    }

    @Nullable
    /* renamed from: component4, reason: from getter */
    public final Integer getStatus() {
        return this.status;
    }

    @NotNull
    public final LoginResponse copy(boolean success, @NotNull LoginData data, @NotNull com.penpencil.core.network.result.Error error, @Nullable Integer status) {
        Intrinsics.checkNotNullParameter(data, "data");
        Intrinsics.checkNotNullParameter(error, "error");
        return new LoginResponse(success, data, error, status);
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof LoginResponse)) {
            return false;
        }
        LoginResponse loginResponse = (LoginResponse) other;
        return this.success == loginResponse.success && Intrinsics.f(this.data, loginResponse.data) && Intrinsics.f(this.error, loginResponse.error) && Intrinsics.f(this.status, loginResponse.status);
    }

    @NotNull
    public final LoginData getData() {
        return this.data;
    }

    @NotNull
    public final com.penpencil.core.network.result.Error getError() {
        return this.error;
    }

    @Nullable
    public final Integer getStatus() {
        return this.status;
    }

    public final boolean getSuccess() {
        return this.success;
    }

    public int hashCode() {
        int hashCode = (this.error.hashCode() + ((this.data.hashCode() + ((this.success ? 1231 : 1237) * 31)) * 31)) * 31;
        Integer num = this.status;
        return hashCode + (num == null ? 0 : num.hashCode());
    }

    public final void setStatus(@Nullable Integer num) {
        this.status = num;
    }

    @NotNull
    public String toString() {
        return "LoginResponse(success=" + this.success + ", data=" + this.data + ", error=" + this.error + ", status=" + this.status + ")";
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(@NotNull Parcel dest, int flags) {
        Intrinsics.checkNotNullParameter(dest, "dest");
        dest.writeInt(this.success ? 1 : 0);
        this.data.writeToParcel(dest, flags);
        dest.writeParcelable(this.error, flags);
        Integer num = this.status;
        if (num == null) {
            dest.writeInt(0);
        } else {
            y930.a(dest, 1, num);
        }
    }
}
