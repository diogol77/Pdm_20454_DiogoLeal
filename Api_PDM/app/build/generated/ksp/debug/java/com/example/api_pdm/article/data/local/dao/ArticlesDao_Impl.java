package com.example.api_pdm.article.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.api_pdm.article.data.local.database.ArticleEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ArticlesDao_Impl implements ArticlesDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfClearDatabase;

  private final EntityUpsertionAdapter<ArticleEntity> __upsertionAdapterOfArticleEntity;

  public ArticlesDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfClearDatabase = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM articleentity";
        return _query;
      }
    };
    this.__upsertionAdapterOfArticleEntity = new EntityUpsertionAdapter<ArticleEntity>(new EntityInsertionAdapter<ArticleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `ArticleEntity` (`articleId`,`title`,`description`,`content`,`pubDate`,`sourceName`,`imageUrl`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ArticleEntity entity) {
        statement.bindString(1, entity.getArticleId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, entity.getPubDate());
        statement.bindString(6, entity.getSourceName());
        statement.bindString(7, entity.getImageUrl());
      }
    }, new EntityDeletionOrUpdateAdapter<ArticleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `ArticleEntity` SET `articleId` = ?,`title` = ?,`description` = ?,`content` = ?,`pubDate` = ?,`sourceName` = ?,`imageUrl` = ? WHERE `articleId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ArticleEntity entity) {
        statement.bindString(1, entity.getArticleId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, entity.getPubDate());
        statement.bindString(6, entity.getSourceName());
        statement.bindString(7, entity.getImageUrl());
        statement.bindString(8, entity.getArticleId());
      }
    });
  }

  @Override
  public Object clearDatabase(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDatabase.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearDatabase.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertArticleList(final List<ArticleEntity> articleList,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfArticleEntity.upsert(articleList);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getArticleList(final Continuation<? super List<ArticleEntity>> $completion) {
    final String _sql = "SELECT * FROM articleentity";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ArticleEntity>>() {
      @Override
      @NonNull
      public List<ArticleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfArticleId = CursorUtil.getColumnIndexOrThrow(_cursor, "articleId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPubDate = CursorUtil.getColumnIndexOrThrow(_cursor, "pubDate");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final List<ArticleEntity> _result = new ArrayList<ArticleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ArticleEntity _item;
            final String _tmpArticleId;
            _tmpArticleId = _cursor.getString(_cursorIndexOfArticleId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPubDate;
            _tmpPubDate = _cursor.getString(_cursorIndexOfPubDate);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            _item = new ArticleEntity(_tmpArticleId,_tmpTitle,_tmpDescription,_tmpContent,_tmpPubDate,_tmpSourceName,_tmpImageUrl);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getArticle(final String articleId,
      final Continuation<? super ArticleEntity> $completion) {
    final String _sql = "SELECT * FROM articleentity WHERE articleId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, articleId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ArticleEntity>() {
      @Override
      @Nullable
      public ArticleEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfArticleId = CursorUtil.getColumnIndexOrThrow(_cursor, "articleId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfPubDate = CursorUtil.getColumnIndexOrThrow(_cursor, "pubDate");
          final int _cursorIndexOfSourceName = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceName");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "imageUrl");
          final ArticleEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpArticleId;
            _tmpArticleId = _cursor.getString(_cursorIndexOfArticleId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpPubDate;
            _tmpPubDate = _cursor.getString(_cursorIndexOfPubDate);
            final String _tmpSourceName;
            _tmpSourceName = _cursor.getString(_cursorIndexOfSourceName);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            _result = new ArticleEntity(_tmpArticleId,_tmpTitle,_tmpDescription,_tmpContent,_tmpPubDate,_tmpSourceName,_tmpImageUrl);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
