# JavaDounat

## チームメンバーが最初にすること

### 1. Gitを設定する

このPCで初めてGitを使う場合だけ、名前とメールアドレスを設定します。これは、誰がコミットしたか分かるようにするための設定です。

```bash
git config --global user.name "自分の名前またはGitHubユーザー名"
git config --global user.email "GitHubに登録したメールアドレス"
```

すでに設定済みの場合は、もう一度実行する必要はありません。次のコマンドで確認できます。

```bash
git config --global --list
```

### 2. プロジェクトをクローンする

GitHubのリポジトリ画面で「Code」からHTTPSのURLをコピーし、保存したい場所で実行します。

```bash
git clone https://github.com/tanirup/JavaDounat.git
cd JavaDounat
```

## 毎回の作業手順

### 1. 作業前に最新状態を取り込む

```bash
git switch main
git pull origin main
```

### 2. 自分の作業用ブランチを作る

ブランチとは、`main`を直接変更せずに作業するための「自分専用の作業場所」です。失敗しても`main`にはすぐ影響しないため、チーム開発ではブランチを使います。

最初に、担当する作業が分かる名前でブランチを作ります。

```bash
git switch -c feature-作業名
```

たとえば、ログイン画面を作る場合：

```bash
git switch -c feature-login
```

このコマンドは、`feature-login`というブランチを新しく作り、そのブランチへ移動するという意味です。現在いるブランチを確認するときは、次を実行します。

```bash
git branch
```

先頭に`*`が付いているものが、現在作業中のブランチです。

```text
  main
* feature-login
```

ブランチ名の例：

- ログイン画面：`feature-login`
- 投稿機能：`feature-post`
- デザイン修正：`fix-design`

> **作業の流れ：** mainを最新にする → 自分のブランチを作る → 編集する → pushする → Pull Requestを作る

### 3. コードを編集して変更を確認する

```bash
git status
git diff
```

### 4. 変更を記録する

```bash
git add .
git commit -m "変更内容を書く"
```

コミットメッセージには、「修正」だけではなく、何を変更したかを書いてください。

例：

```bash
git commit -m "ログイン画面を追加"
```

### 5. GitHubへプッシュする

最初のプッシュ：

```bash
git push -u origin feature-作業名
```

同じブランチで2回目以降：

```bash
git push
```

### 6. Pull Requestを作る

Pull Requestは、「自分のブランチで行った変更を`main`へ入れてよいか確認してもらう機能」です。

1. GitHubで対象のリポジトリを開きます。
2. 「Compare & pull request」を押します。
3. 何を変更したか入力してPull Requestを作ります。
4. チームメンバーに内容を確認してもらいます。
5. 問題がなければ`main`へマージします。

マージ後に次の作業を始めるときは、再び`main`へ戻して最新状態を取り込み、新しいブランチを作ります。

```bash
git switch main
git pull origin main
git switch -c feature-次の作業名
```

## チーム内のルール

- 作業を始める前に、必ず`main`で`git pull origin main`を実行する。
- `main`へ直接プッシュせず、作業用ブランチを使う。
- コミットメッセージには変更内容を分かりやすく書く。
- `.env`、パスワード、APIキー、秘密鍵は絶対にプッシュしない。
- 担当するファイルや機能を事前に共有する。
- 分からないエラーやコンフリクトが出たら、無理に操作せずチームに相談する。

## よく使うコマンド

```bash
git status                         # 変更状況を確認
git switch main                    # mainへ移動
git pull origin main               # 最新状態を取り込む
git switch -c feature-作業名       # 作業用ブランチを作成
git add .                          # 変更を追加
git commit -m "変更内容"           # 変更を記録
git push -u origin feature-作業名  # 初回のプッシュ
git push                           # 2回目以降のプッシュ
```
